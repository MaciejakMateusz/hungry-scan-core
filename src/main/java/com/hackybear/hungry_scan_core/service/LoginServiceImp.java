package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.JwtService;
import com.hackybear.hungry_scan_core.service.interfaces.LoginService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginServiceImp implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @Value("${IS_PROD}")
    private boolean isProduction;

    @Value("${JWT_EXPIRATION_MILLIS}")
    private long expirationMillis;

    public LoginServiceImp(AuthenticationManager authenticationManager,
                           UserService userService,
                           JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public ResponseEntity<?> handleLogin(AuthRequestDTO authRequestDTO, HttpServletResponse response) throws LocalizedException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "unauthorized"));
        } else if (userService.isEnabled(authRequestDTO.getUsername()) == 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "notActivated"));
        }
        String jwt = jwtService.generateToken(authRequestDTO.getUsername());
        String jwtCookie = prepareJwtCookie(jwt);
        response.addHeader("Set-Cookie", jwtCookie);
        if (!userService.hasCreatedRestaurant(authRequestDTO.getUsername())) {
            return ResponseEntity.ok(Map.of("redirectUrl", "/create-restaurant"));
        }
        return ResponseEntity.ok(Map.of("redirectUrl", "/cms"));
    }

    private String prepareJwtCookie(String jwt) {
        long expirationTimeSeconds = expirationMillis / 1000;
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .path("/")
                .httpOnly(true)
                .secure(isProduction)
                .maxAge(expirationTimeSeconds)
                .sameSite("Strict")
                .build();
        return cookie.toString();
    }

}
