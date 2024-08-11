package com.hackybear.hungry_scan_core.controller.login;

import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.dto.JwtResponseDTO;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final RestaurantTableService restaurantTableService;
    private final SettingsService settingsService;
    private final UserService userService;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final ExceptionHelper exceptionHelper;

    public UserController(AuthenticationManager authenticationManager,
                          RestaurantTableService restaurantTableService,
                          SettingsService settingsService,
                          UserService userService,
                          RoleService roleService,
                          JwtService jwtService, ExceptionHelper exceptionHelper) {
        this.authenticationManager = authenticationManager;
        this.restaurantTableService = restaurantTableService;
        this.settingsService = settingsService;
        this.userService = userService;
        this.roleService = roleService;
        this.jwtService = jwtService;
        this.exceptionHelper = exceptionHelper;
    }

    @PostMapping("/login")
    public JwtResponseDTO login(@RequestBody AuthRequestDTO authRequestDTO) {
        Settings settings = settingsService.getSettings();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            //todo zamienić cs jwt na ss jwt cookie handling

            //            String jwt = jwtService.generateToken(authRequestDTO.getUsername(),
            //                    settings.getEmployeeSessionTime());
            //            Long maxAgeInSeconds = settings.getEmployeeSessionTime() * 3600;
            //
            //            String cookieValue = String.format("jwt=%s; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=%d",
            //                    jwt, maxAgeInSeconds);
            //            response.setHeader("Set-Cookie", cookieValue);
            return JwtResponseDTO
                    .builder()
                    .accessToken(jwtService.generateToken(authRequestDTO.getUsername(),
                            settings.getEmployeeSessionTime())).build();
        } else {
            throw new UsernameNotFoundException("Not authorized - invalid user request");
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        String cookieValue = "jwt=; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=0";
        response.setHeader("Set-Cookie", cookieValue);
    }

    @GetMapping("/scan/{token}")
    public JwtResponseDTO scanTableQr(@PathVariable("token") String token) throws AccessDeniedException, LocalizedException {
        Settings settings = settingsService.getSettings();
        RestaurantTable table = getRestaurantTable(token);
        if (table.isActive()) {
            String randomUUID = UUID.randomUUID().toString();
            String accessToken = jwtService.generateToken(randomUUID.substring(1, 13),
                    settings.getEmployeeSessionTime());
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
    public ResponseEntity<Void> scanBasicQr(HttpServletResponse response) throws IOException {
        Settings settings = settingsService.getSettings();
        String randomUUID = UUID.randomUUID().toString();
        String accessToken = jwtService.generateToken(randomUUID.substring(0, 12),
                settings.getEmployeeSessionTime());
        persistUser(new JwtToken(accessToken), randomUUID);

        // Construct the URL to redirect to, including the access token as a query parameter
        String redirectUrl = UriComponentsBuilder.fromUriString("https://your-application-url.com")
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();

        // Perform the redirection
        response.sendRedirect(redirectUrl);
        return ResponseEntity.status(HttpServletResponse.SC_FOUND).build();
    }

    private void persistTableAndUser(JwtToken jwtToken, String uuid, RestaurantTable restaurantTable) {
        String username = uuid.substring(1, 13);
        boolean isFirstCustomer = restaurantTable.getUsers().isEmpty();
        userService.save(createTempCustomer(jwtToken, username, isFirstCustomer));
        User customer = userService.findByUsername(username);
        restaurantTable.addCustomer(customer);
        restaurantTableService.save(restaurantTable);
    }

    private void persistUser(JwtToken jwtToken, String uuid) {
        String username = uuid.substring(1, 13);
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

    private User createTempCustomer(JwtToken jwtToken, String username, boolean isFirstCustomer) {
        User customer = new User();
        customer.setUsername(username);
        customer.setEmail(username + "@temp.it");
        customer.setPassword(UUID.randomUUID().toString());
        Role role = roleService.findByName(isFirstCustomer ? "ROLE_CUSTOMER" : "ROLE_CUSTOMER_READONLY");
        customer.setRoles(new HashSet<>(Collections.singletonList(role)));
        customer.setJwtToken(jwtToken);
        return customer;
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}