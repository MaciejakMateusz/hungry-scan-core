package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.JwtService;
import com.hackybear.hungry_scan_core.service.interfaces.LoginService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoginServiceImp implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ExceptionHelper exceptionHelper;
    private final ResponseHelper responseHelper;

    @Value("${IS_PROD}")
    private boolean isProduction;

    @Value("${JWT_EXPIRATION_MILLIS}")
    private long expirationMillis;

    @Override
    public ResponseEntity<?> handleLogin(AuthRequestDTO authRequestDTO, HttpServletResponse response) throws LocalizedException {
        ResponseEntity<?> invalidResponse = validateLogin(authRequestDTO, response);
        if (Objects.nonNull(invalidResponse)) return invalidResponse;
        markSignIn(authRequestDTO.getUsername());
        return prepareLoginResponse(authRequestDTO, response);
    }

    @Override
    @Transactional
    public ResponseEntity<?> handleLogout(HttpServletResponse response, String username) {
        String invalidatedJwtCookie = invalidateJwtCookie();
        response.setHeader("Set-Cookie", invalidatedJwtCookie);
        try {
            User user = userService.findByUsername(username);
            user.setSignedIn(false);
            userRepository.save(user);
        } catch (LocalizedException e) {
            responseHelper.createErrorResponse(e);
        }
        return ResponseEntity.ok().body(Map.of("redirectUrl", "/sign-in"));
    }

    private ResponseEntity<?> validateLogin(AuthRequestDTO authRequestDTO, HttpServletResponse response) throws LocalizedException {
        ResponseEntity<?> invalidResponse = null;
        String username = authRequestDTO.getUsername().trim();
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, authRequestDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (!authentication.isAuthenticated()) {
            invalidResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "unauthorized"));
        } else if (userService.isEnabled(username) == 0) {
            invalidResponse = ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "notActivated"));
        } else if (!userService.isActive(username)) {
            invalidResponse = ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "accountInactive"));
        } else if (!userService.hasCreatedRestaurant(username)) {
            prepareJwtCookie(authRequestDTO, response);
            invalidResponse = ResponseEntity.ok(Map.of("redirectUrl", "/create-restaurant"));
        }
        return invalidResponse;
    }

    private ResponseEntity<?> prepareLoginResponse(AuthRequestDTO authRequestDTO, HttpServletResponse response) {
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
        String jwt = jwtService.generateToken(authRequestDTO.getUsername().trim());
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
                .sameSite(isProduction ? "None" : "Strict")
                .build();
        return cookie.toString();
    }

    private void markSignIn(String username) throws LocalizedException {
        User user = userService.findByUsername(username);
        user.setLastLoginAt(LocalDateTime.now());
        user.setSignedIn(true);
        userRepository.save(user);
    }

    private String invalidateJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .path("/")
                .httpOnly(true)
                .secure(isProduction)
                .maxAge(0)
                .sameSite(isProduction ? "None" : "Strict")
                .build();
        return cookie.toString();
    }
}