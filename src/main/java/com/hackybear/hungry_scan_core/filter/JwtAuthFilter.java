package com.hackybear.hungry_scan_core.filter;

import com.hackybear.hungry_scan_core.service.CustomUserDetailsService;
import com.hackybear.hungry_scan_core.service.interfaces.JwtService;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@NonNullApi
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${IS_PROD}")
    private boolean isProduction;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Map<String, String> jwtParams = getJwtParams(request);
        String token = jwtParams.get("token");
        String username = jwtParams.get("username");

        if (Objects.isNull(token)) {
            String authHeader = request.getHeader("Authorization");
            if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
            }
        } else if ("/api/user/register".equals(request.getRequestURI())) {
            String invalidatedJwtCookie = invalidateJwtCookie();
            response.setHeader("Set-Cookie", invalidatedJwtCookie);
            filterChain.doFilter(request, response);
            return;
        }

        setUpSecurityContext(username, token, request);
        filterChain.doFilter(request, response);
    }

    private Map<String, String> getJwtParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                    .filter(cookie -> "jwt".equals(cookie.getName()))
                    .findFirst();
            if (jwtCookie.isPresent()) {
                String token = jwtCookie.get().getValue();
                params.put("token", token);
                params.put("username", jwtService.extractUsername(token));
            }
        }
        return params;
    }

    private String invalidateJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .path("/")
                .httpOnly(true)
                .secure(isProduction)
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return cookie.toString();
    }

    private void setUpSecurityContext(@Nullable String username, String token, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(username) && Objects.isNull(authentication)) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authenticationToken);
            }
        }
    }
}