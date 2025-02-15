package com.hackybear.hungry_scan_core.filter;

import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
@NonNullApi
interface FilterBase {

    default Map<String, String> getJwtParams(HttpServletRequest request, Function<String, String> getUsername) {
        Map<String, String> params = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                    .filter(cookie -> "jwt".equals(cookie.getName()))
                    .findFirst();
            if (jwtCookie.isPresent()) {
                String token = jwtCookie.get().getValue();
                params.put("token", token);
                params.put("username", getUsername.apply(token));
            }
        }
        return params;
    }

    default String invalidateJwtCookie(boolean isSecure) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .path("/")
                .httpOnly(true)
                .secure(isSecure)
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return cookie.toString();
    }
}
