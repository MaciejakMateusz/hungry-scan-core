package com.hackybear.hungry_scan_core.controller.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:3001",
        "http://localhost:3002",
        "http://localhost:3003"})
public class AuthController {

    @GetMapping("/restaurant")
    @PreAuthorize("isAuthenticated()")
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