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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@NonNullApi
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter implements FilterBase {

    private static final String SECONDARY_COOKIE = "menu_jwt";
    private static final List<String> nonMenuPaths = List.of("/api/auth/app", "/api/auth/anonymous");

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final List<String> jwtInvalidateURIs;

    @Value("${IS_PROD}")
    private boolean isProduction;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Map<String, String> jwtParams = getJwtParams(request, jwtService::extractUsername);
        String token = jwtParams.get("token");
        String username = jwtParams.get("username");

        if ((token == null || token.isBlank())) {
            String secondaryToken = resolveTokenFromCookie(request);
            if (secondaryToken != null && !secondaryToken.isBlank() && !nonMenuPaths.contains(request.getRequestURI())) {
                token = secondaryToken;
                username = jwtService.extractUsername(token);
            } else {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (jwtInvalidateURIs.contains(request.getRequestURI())) {
            String invalidatedJwtCookie = invalidateJwtCookie(isProduction);
            response.setHeader("Set-Cookie", invalidatedJwtCookie);
            filterChain.doFilter(request, response);
            return;
        }

        setUpSecurityContext(username, token, request);
        filterChain.doFilter(request, response);
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

    @Nullable
    private String resolveTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (SECONDARY_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
