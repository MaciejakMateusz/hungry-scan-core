package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.service.interfaces.UserService;
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
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/restaurant")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public boolean restaurantAuth() {
        return true;
    }

    @GetMapping("/cms")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> cmsAuth() {
        return userService.hasCreatedRestaurant() ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.FOUND).body(Map.of("redirectUrl", "/create-restaurant"));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> dashboardAuth() {
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
                ResponseEntity.status(HttpStatus.FOUND).body(Map.of("redirectUrl", "/dashboard")) :
                ResponseEntity.ok().build();
    }

    @GetMapping("/activation")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> activationAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activation/*")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> activationAllAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activation-error")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> activationErrorAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account-activated")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> accountActivatedAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sign-in")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> signInAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sign-up")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> signUpAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/password-recovery")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> passwordRecoveryAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/new-password")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> newPasswordAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recovery-sent")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> recoverySentAuth() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recovery-confirmation")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> recoveryConfirmationAuth() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}