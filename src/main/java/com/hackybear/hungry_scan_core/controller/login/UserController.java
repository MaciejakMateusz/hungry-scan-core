package com.hackybear.hungry_scan_core.controller.login;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.dto.JwtResponseDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.JwtToken;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.JwtService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("http://localhost:3002")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final RestaurantTableService restaurantTableService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final ExceptionHelper exceptionHelper;
    private final ResponseHelper responseHelper;

    @Value("${customer.app.port}")
    private String customerAppPort;

    public UserController(AuthenticationManager authenticationManager,
                          RestaurantTableService restaurantTableService,
                          UserService userService, UserMapper userMapper,
                          RoleService roleService,
                          JwtService jwtService,
                          ExceptionHelper exceptionHelper,
                          ResponseHelper responseHelper) {
        this.authenticationManager = authenticationManager;
        this.restaurantTableService = restaurantTableService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.jwtService = jwtService;
        this.exceptionHelper = exceptionHelper;
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
            String jwtCookie = prepareJwtCookie(authRequestDTO);
            response.addHeader("Set-Cookie", jwtCookie);
            return ResponseEntity.ok().body(Map.of("message", "Login successful"));
        } else {
            throw new UsernameNotFoundException("Not authorized - invalid user request");
        }
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

    @Deprecated //TODO metoda do wywalenia po zmianach w założeniach skanowania kodów
    @GetMapping("/scan/{token}")
    public JwtResponseDTO scanTableQr(@PathVariable("token") String token) throws AccessDeniedException, LocalizedException {
        RestaurantTable table = getRestaurantTable(token);
        if (table.isActive()) {
            String randomUUID = UUID.randomUUID().toString();
            String accessToken = jwtService.generateToken(randomUUID.substring(1, 13));
            persistTableAndUser(new JwtToken(accessToken), randomUUID, table);
            return JwtResponseDTO
                    .builder()
                    .accessToken(accessToken)
                    .build();
        } else {
            throw new LocalizedException(exceptionHelper.getLocalizedMessage(
                    "error.restaurantTableService.tableNotActive",
                    table.getId()));
        }
    }

    @GetMapping("/scan")
    public ResponseEntity<Void> scanQr(HttpServletResponse response) throws IOException {
        String randomUUID = UUID.randomUUID().toString();
        String accessToken = jwtService.generateToken(randomUUID.substring(1, 13));
        persistUser(new JwtToken(accessToken), randomUUID);

        String redirectUrl = UriComponentsBuilder.fromUriString(getCustomerAppUrl())
                .queryParam("token", accessToken)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
        return ResponseEntity.status(HttpServletResponse.SC_FOUND).build();
    }

    private String prepareJwtCookie(AuthRequestDTO authRequestDTO) {
        String jwt = jwtService.generateToken(authRequestDTO.getUsername());

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(3600)
                .sameSite("Strict")
                .build();

        return cookie.toString();
    }

    private String invalidateJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return cookie.toString();
    }

    private void persistTableAndUser(JwtToken jwtToken, String uuid, RestaurantTable restaurantTable) throws LocalizedException {
        String username = uuid.substring(1, 13) + "@temp.it";
        boolean isFirstCustomer = restaurantTable.getUsers().isEmpty();
        userService.save(createTempCustomer(jwtToken, username, isFirstCustomer));
        User customer = userService.findByUsername(username);
        restaurantTable.addCustomer(customer);
        restaurantTableService.save(restaurantTable);
    }

    private void persistUser(JwtToken jwtToken, String uuid) {
        String username = uuid.substring(1, 13) + "@temp.it";
        userService.save(createTempCustomer(jwtToken, username, false));
    }

    private RestaurantTable getRestaurantTable(String token) throws AccessDeniedException {
        RestaurantTable restaurantTable;
        try {
            restaurantTable = restaurantTableService.findByToken(token);
        } catch (LocalizedException e) {
            throw new AccessDeniedException(e.getLocalizedMessage());
        }
        return restaurantTable;
    }

    private RegistrationDTO createTempCustomer(JwtToken jwtToken, String username, boolean isFirstCustomer) {
        User customer = new User();
        customer.setUsername(username);
        customer.setEmail(username);
        customer.setName(username.substring(4));
        customer.setSurname(username.substring(4));
        customer.setPassword(UUID.randomUUID().toString());
        Role role = roleService.findByName(isFirstCustomer ? "ROLE_CUSTOMER" : "ROLE_CUSTOMER_READONLY");
        customer.setRoles(new HashSet<>(Collections.singletonList(role)));
        customer.setJwtToken(jwtToken);
        return userMapper.toDTO(customer);
    }

    private String getCustomerAppUrl() {
        return "http://" +
                getServerIPAddress() +
                ":" +
                customerAppPort +
                "/menu";
    }

    private String getServerIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Failed to determine server IP address: " + e.getMessage());
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}