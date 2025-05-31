package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.hackybear.hungry_scan_core.utility.Fields.ROLES_EXCEPT_CUSTOMER;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/restaurant")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public boolean restaurantAuth() {
        return true;
    }

    @GetMapping("/app")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> appAuth() {
        return userService.hasCreatedRestaurant() ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.FOUND).body(Map.of("redirectUrl", "/create-restaurant"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAuth() {
        return userService.hasCreatedRestaurant() ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.FOUND).body(Map.of("redirectUrl", "/create-restaurant"));
    }

    @GetMapping("/create-restaurant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRestaurantAuth() {
        return userService.hasCreatedRestaurant() ?
                ResponseEntity.status(HttpStatus.FOUND).body(Map.of("redirectUrl", "/app")) :
                ResponseEntity.ok().build();
    }

    @GetMapping("/anonymous")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> isAnonymous() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/anonymous/*")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> isAnonymousSub() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}