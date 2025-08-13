package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.service.interfaces.AuthService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserService userService;

    @Override
    public ResponseEntity<?> authorizeApp() {
        if (userService.hasCreatedRestaurant()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(Map.of("redirectUrl", "/create-restaurant"));
    }

    @Override
    public ResponseEntity<?> authorizeCreateRestaurant() {
        if (userService.hasCreatedRestaurant()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(Map.of("redirectUrl", "/app"));
        }
        return ResponseEntity.ok().build();
    }
}
