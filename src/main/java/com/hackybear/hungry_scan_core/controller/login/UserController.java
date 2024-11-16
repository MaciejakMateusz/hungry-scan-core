package com.hackybear.hungry_scan_core.controller.login;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.entity.JwtToken;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.JwtService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantService;
import com.hackybear.hungry_scan_core.service.interfaces.RoleService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("http://localhost:3002")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final UserService userService;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final ResponseHelper responseHelper;

    @Value("${CUSTOMER_APP_URL}")
    private String customerAppUrl;

    @Value("${IS_PROD}")
    private boolean isProduction;

    public UserController(AuthenticationManager authenticationManager,
                          RestaurantService restaurantService, RestaurantMapper restaurantMapper,
                          UserService userService,
                          RoleService roleService,
                          JwtService jwtService,
                          ResponseHelper responseHelper) {
        this.authenticationManager = authenticationManager;
        this.restaurantService = restaurantService;
        this.restaurantMapper = restaurantMapper;
        this.userService = userService;
        this.roleService = roleService;
        this.jwtService = jwtService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> add(@Valid @RequestBody RegistrationDTO registrationDTO, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        Map<String, Object> errorParams = responseHelper.getErrorParams(registrationDTO);
        if (!errorParams.isEmpty()) {
            return ResponseEntity.badRequest().body(errorParams);
        }
        return responseHelper.getResponseEntity(registrationDTO, userService::save);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            String jwt = jwtService.generateToken(authRequestDTO.getUsername());
            String jwtCookie = prepareJwtCookie(jwt, 28800, "Strict");
            response.addHeader("Set-Cookie", jwtCookie);
            return ResponseEntity.ok().body(Map.of("message", "Login successful"));
        } else {
            throw new UsernameNotFoundException("Not authorized - invalid user request");
        }
    }

    @GetMapping("/scan/{restaurantToken}")
    public ResponseEntity<?> scanQr(HttpServletResponse response, @PathVariable String restaurantToken) throws IOException {
        String username = UUID.randomUUID().toString().substring(1, 13) + "@temp.it";
        String jwt = jwtService.generateToken(username);
        try {
            persistUser(new JwtToken(jwt), username, restaurantToken);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }

        String cookie = prepareJwtCookie(jwt, 10800, "none");
        response.addHeader("Set-Cookie", cookie);
        response.sendRedirect(customerAppUrl);
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        String invalidatedJwtCookie = invalidateJwtCookie();
        response.setHeader("Set-Cookie", invalidatedJwtCookie);
        return ResponseEntity.ok().body(Map.of("message", "Logout successful"));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/restaurant")
    public ResponseEntity<?> switchRestaurant(@RequestBody Long restaurantId) {
        return responseHelper.getResponseEntity(restaurantId, userService::switchRestaurant);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/menu")
    public ResponseEntity<?> switchMenu(@RequestBody Long menuId) {
        return responseHelper.getResponseEntity(menuId, userService::switchMenu);
    }

    private String prepareJwtCookie(String jwt, long maxAge, String sameSite) {
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .path("/")
                .httpOnly(true)
                .secure(isProduction)
                .maxAge(maxAge)
                .sameSite(sameSite)
                .build();
        return cookie.toString();
    }

    private String invalidateJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .path("/")
                .httpOnly(true)
                .secure(isProduction)
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return cookie.toString();
    }

    private void persistUser(JwtToken jwtToken, String username, String restaurantToken) throws LocalizedException {
        User user = createTempCustomer(jwtToken, username, restaurantToken);
        userService.saveTempUser(user);
    }

    private User createTempCustomer(JwtToken jwtToken,
                                    String username,
                                    String restaurantToken) throws LocalizedException {
        User temp = new User();
        RestaurantDTO restaurantDTO = restaurantService.findByToken(restaurantToken);
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO);
        temp.setRestaurants(Set.of(restaurant));
        temp.setActiveRestaurantId(restaurant.getId());
        temp.setOrganizationId(0L);
        temp.setUsername(username);
        temp.setEmail(username);
        temp.setName(username.substring(4));
        temp.setSurname(username.substring(4));
        temp.setPassword(UUID.randomUUID().toString());
        Role role = roleService.findByName("ROLE_CUSTOMER_READONLY");
        temp.setRoles(new HashSet<>(Collections.singletonList(role)));
        temp.setJwtToken(jwtToken);
        return temp;
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}