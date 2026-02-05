package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.JwtService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImpTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private ExceptionHelper exceptionHelper;
    @Mock
    private ResponseHelper responseHelper;

    @InjectMocks
    private LoginServiceImp sut;

    @Mock
    private HttpServletResponse servletResponse;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sut, "isProduction", false);
        ReflectionTestUtils.setField(sut, "expirationMillis", 3_600_000L);
    }

    @Test
    void handleLogin_whenNotAuthenticated_returns401_andDoesNotSetCookie_orSaveUser() throws Exception {
        AuthRequestDTO dto = mock(AuthRequestDTO.class);
        when(dto.getUsername()).thenReturn(" alice ");
        when(dto.getPassword()).thenReturn("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> res = sut.handleLogin(dto, servletResponse);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(res.getBody()).isEqualTo(Map.of("message", "unauthorized"));

        verify(servletResponse, never()).addHeader(eq("Set-Cookie"), anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userService, never()).findByUsername(anyString());
    }

    @Test
    void handleLogin_whenNotActivated_returns403_andDoesNotSetCookie_orSaveUser() throws Exception {
        AuthRequestDTO dto = mock(AuthRequestDTO.class);
        when(dto.getUsername()).thenReturn("bob");
        when(dto.getPassword()).thenReturn("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(userService.isEnabled("bob")).thenReturn(0);

        ResponseEntity<?> res = sut.handleLogin(dto, servletResponse);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody()).isEqualTo(Map.of("message", "notActivated"));

        verify(servletResponse, never()).addHeader(eq("Set-Cookie"), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void handleLogin_whenAccountInactive_returns403_andDoesNotSetCookie_orSaveUser() throws Exception {
        AuthRequestDTO dto = mock(AuthRequestDTO.class);
        when(dto.getUsername()).thenReturn("carol");
        when(dto.getPassword()).thenReturn("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(userService.isEnabled("carol")).thenReturn(1);
        when(userService.isActive("carol")).thenReturn(false);

        ResponseEntity<?> res = sut.handleLogin(dto, servletResponse);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody()).isEqualTo(Map.of("message", "accountInactive"));

        verify(servletResponse, never()).addHeader(eq("Set-Cookie"), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void handleLogin_whenNoRestaurant_setsJwtCookie_andReturnsRedirectToCreateRestaurant_andDoesNotMarkSignIn() throws Exception {
        AuthRequestDTO dto = mock(AuthRequestDTO.class);
        when(dto.getUsername()).thenReturn("dave");
        when(dto.getPassword()).thenReturn("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(userService.isEnabled("dave")).thenReturn(1);
        when(userService.isActive("dave")).thenReturn(true);
        when(userService.hasCreatedRestaurant("dave")).thenReturn(false);

        when(jwtService.generateToken("dave")).thenReturn("token123");

        ResponseEntity<?> res = sut.handleLogin(dto, servletResponse);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(Map.of("redirectUrl", "/create-restaurant"));

        verify(servletResponse).addHeader(eq("Set-Cookie"), contains("jwt=token123"));
        verify(servletResponse).addHeader(eq("Set-Cookie"), contains("Max-Age=3600"));
        verify(servletResponse).addHeader(eq("Set-Cookie"), contains("SameSite=Strict"));

        verify(userService, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void handleLogin_whenValid_loginMarksSignIn_setsCookie_andReturnsForenameAndRedirect() throws Exception {
        AuthRequestDTO dto = mock(AuthRequestDTO.class);
        when(dto.getUsername()).thenReturn("erin");
        when(dto.getPassword()).thenReturn("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(userService.isEnabled("erin")).thenReturn(1);
        when(userService.isActive("erin")).thenReturn(true);
        when(userService.hasCreatedRestaurant("erin")).thenReturn(true);

        when(jwtService.generateToken("erin")).thenReturn("jwt456");

        User user = new User();
        user.setForename("Erin");
        when(userService.findByUsername("erin")).thenReturn(user);

        ResponseEntity<?> res = sut.handleLogin(dto, servletResponse);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(Map.of(
                "forename", "Erin",
                "redirectUrl", "/app"
        ));

        verify(servletResponse).addHeader(eq("Set-Cookie"), contains("jwt=jwt456"));
        verify(servletResponse).addHeader(eq("Set-Cookie"), contains("Max-Age=3600"));

        ArgumentCaptor<User> savedUserCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(savedUserCaptor.capture());
        User saved = savedUserCaptor.getValue();
        assertThat(saved.isSignedIn()).isTrue();
        assertThat(saved.getLastLoginAt()).isNotNull();
    }

    @Test
    void handleLogin_whenGetPostLoginParamsThrows_returns400_withLocalizedMessage_andDoesNotSetCookie_butStillMarksSignIn() throws Exception {
        AuthRequestDTO dto = mock(AuthRequestDTO.class);
        when(dto.getUsername()).thenReturn("frank");
        when(dto.getPassword()).thenReturn("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(userService.isEnabled("frank")).thenReturn(1);
        when(userService.isActive("frank")).thenReturn(true);
        when(userService.hasCreatedRestaurant("frank")).thenReturn(true);

        User signInUser = new User();
        when(userService.findByUsername("frank"))
                .thenReturn(signInUser)
                .thenThrow(new LocalizedException("boom"));

        when(exceptionHelper.getLocalizedMsg("error.login.params")).thenReturn("localized login params error");

        ResponseEntity<?> res = sut.handleLogin(dto, servletResponse);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isEqualTo(Map.of("error", "localized login params error"));

        verify(servletResponse, never()).addHeader(eq("Set-Cookie"), anyString());

        verify(userRepository, times(1)).save(any(User.class));
        assertThat(signInUser.isSignedIn()).isTrue();
        assertThat(signInUser.getLastLoginAt()).isNotNull();
    }

    @Test
    void handleLogout_setsInvalidatedCookie_updatesUserSignedInFalse_andReturnsRedirect() throws Exception {
        User user = new User();
        user.setSignedIn(true);

        when(userService.findByUsername("greg")).thenReturn(user);

        ResponseEntity<?> res = sut.handleLogout(servletResponse, "greg");

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(Map.of("redirectUrl", "/sign-in?logout=true"));

        verify(servletResponse).setHeader(eq("Set-Cookie"), contains("jwt="));
        verify(servletResponse).setHeader(eq("Set-Cookie"), contains("Max-Age=0"));
        verify(servletResponse).setHeader(eq("Set-Cookie"), contains("SameSite=Strict"));

        verify(userRepository, times(1)).save(user);
        assertThat(user.isSignedIn()).isFalse();
    }

    @Test
    void handleInactivityLogout_setsInvalidatedCookie_andReturnsInactiveRedirect() throws Exception {
        User user = new User();
        when(userService.findByUsername("helen")).thenReturn(user);

        ResponseEntity<?> res = sut.handleInactivityLogout(servletResponse, "helen");

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(Map.of("redirectUrl", "/sign-in?inactive=true"));

        verify(servletResponse).setHeader(eq("Set-Cookie"), contains("Max-Age=0"));
        verify(userRepository).save(user);
        assertThat(user.isSignedIn()).isFalse();
    }

    @Test
    void handleLogout_whenFindByUsernameThrows_callsResponseHelper_andStillReturnsOkRedirect() throws Exception {
        when(userService.findByUsername("ivan")).thenThrow(new LocalizedException("nope"));

        ResponseEntity<?> res = sut.handleLogout(servletResponse, "ivan");

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(Map.of("redirectUrl", "/sign-in?logout=true"));

        verify(responseHelper, times(1)).createErrorResponse(any(LocalizedException.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
