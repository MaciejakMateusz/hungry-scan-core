package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
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

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImp implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final ExceptionHelper exceptionHelper;

    @Value("${IS_PROD}")
    private boolean isProduction;

    @Value("${JWT_EXPIRATION_MILLIS}")
    private long expirationMillis;

    public LoginServiceImp(AuthenticationManager authenticationManager,
                           UserService userService,
                           JwtService jwtService,
                           ExceptionHelper exceptionHelper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public ResponseEntity<?> handleLogin(AuthRequestDTO authRequestDTO, HttpServletResponse response) throws LocalizedException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "unauthorized"));
        } else if (userService.isEnabled(authRequestDTO.getUsername()) == 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "notActivated"));
        } else if (!userService.hasCreatedRestaurant(authRequestDTO.getUsername())) {
            prepareJwtCookie(authRequestDTO, response);
            return ResponseEntity.ok(Map.of("redirectUrl", "/create-restaurant"));
        }
        return prepareInitialResponse(authRequestDTO, response);
    }

    private ResponseEntity<?> prepareInitialResponse(AuthRequestDTO authRequestDTO, HttpServletResponse response) {
        Map<String, Object> initialParams;
        try {
            initialParams = getPostLoginParams(authRequestDTO);
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest().body(Map.of("error",
                    exceptionHelper.getLocalizedMsg("error.login.params")));
        }
        prepareJwtCookie(authRequestDTO, response);
        return ResponseEntity.ok(initialParams);
    }

    private Map<String, Object> getPostLoginParams(AuthRequestDTO authRequestDTO) throws LocalizedException {
        Map<String, Object> params = new HashMap<>();
        User user = userService.findByUsername(authRequestDTO.getUsername());
        params.put("forename", user.getForename());
        params.put("redirectUrl", "/app");
        return params;
    }

    private void prepareJwtCookie(AuthRequestDTO authRequestDTO, HttpServletResponse response) {
        String jwt = jwtService.generateToken(authRequestDTO.getUsername());
        String jwtCookie = getJwtCookie(jwt);
        response.addHeader("Set-Cookie", jwtCookie);
    }

    private String getJwtCookie(String jwt) {
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