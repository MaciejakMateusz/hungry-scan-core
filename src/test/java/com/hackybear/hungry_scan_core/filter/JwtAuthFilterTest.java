package com.hackybear.hungry_scan_core.filter;

import com.hackybear.hungry_scan_core.service.CustomUserDetails;
import com.hackybear.hungry_scan_core.service.CustomUserDetailsService;
import com.hackybear.hungry_scan_core.service.interfaces.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    JwtService jwtService;
    @Mock
    CustomUserDetailsService userDetailsService;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain chain;

    @Mock
    CustomUserDetails userDetails;

    JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = spy(new JwtAuthFilter(jwtService, userDetailsService, List.of("/logout")));
        ReflectionTestUtils.setField(filter, "isProduction", false);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_shouldInvalidateJwtWhenUriMatches() throws Exception {
        when(request.getRequestURI()).thenReturn("/logout");
        doReturn(Map.of("token", "ANY", "username", "john"))
                .when(filter).getJwtParams(any(), any());
        doReturn("token=; Max-Age=0; Path=/").when(filter).invalidateJwtCookie(false);

        filter.doFilter(request, response, chain);

        verify(response).setHeader("Set-Cookie", "token=; Max-Age=0; Path=/");
        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "SecurityContext must stay empty on logout");
    }

    @Test
    void doFilter_shouldPopulateSecurityContextForValidToken() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/foo");
        doReturn(Map.of("token", "VALID", "username", "john"))
                .when(filter).getJwtParams(any(), any());

        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtService.validateToken("VALID", userDetails)).thenReturn(true);

        filter.doFilter(request, response, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication must be set");
        assertEquals(userDetails, auth.getPrincipal());
        assertTrue(auth.isAuthenticated());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_shouldSkipSecurityContextForInvalidToken() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/foo");
        doReturn(Map.of("token", "BAD", "username", "john"))
                .when(filter).getJwtParams(any(), any());

        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtService.validateToken("BAD", userDetails)).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_shouldNotOverwriteExistingAuthentication() throws Exception {
        var existing = new TestingAuthenticationToken("existing", "pwd", "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(existing);

        when(request.getRequestURI()).thenReturn("/api/foo");
        doReturn(Map.of("token", "WHATEVER", "username", "john"))
                .when(filter).getJwtParams(any(), any());

        filter.doFilter(request, response, chain);

        assertSame(existing, SecurityContextHolder.getContext().getAuthentication(),
                "Existing authentication should stay untouched");
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }
}
