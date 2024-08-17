package com.hackybear.hungry_scan_core.controller.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.hackybear.hungry_scan_core.utility.Constants.ROLES_EXCEPT_CUSTOMER;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/restaurant")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public boolean restaurantAuth() {
        return true;
    }

    @GetMapping("/cms")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public boolean cmsAuth() {
        return true;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean adminAuth() {
        return true;
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}