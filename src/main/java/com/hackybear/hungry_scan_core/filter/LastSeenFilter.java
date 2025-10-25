package com.hackybear.hungry_scan_core.filter;

import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.JwtService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
@NonNullApi
@RequiredArgsConstructor
public class LastSeenFilter extends OncePerRequestFilter implements FilterBase {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        Map<String, String> jwtParams = getJwtParams(request, jwtService::extractUsername);
        String username = jwtParams.get("username");

        if (Objects.isNull(username)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            userService.noteActivity(username);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (LocalizedException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        chain.doFilter(request, response);
    }
}