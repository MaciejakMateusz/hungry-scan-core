package com.hackybear.hungry_scan_core.controller.login;

import com.hackybear.hungry_scan_core.annotation.WithRateLimitProtection;
import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.dto.RecoveryDTO;
import com.hackybear.hungry_scan_core.dto.RecoveryInitDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.LoginService;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final LoginService loginService;
    private final QRService qrService;
    private final ExceptionHelper exceptionHelper;
    private final ResponseHelper responseHelper;

    @Value("${IS_PROD}")
    private boolean isProduction;

    public UserController(UserService userService,
                          LoginService loginService,
                          QRService qrService, ExceptionHelper exceptionHelper,
                          ResponseHelper responseHelper) {
        this.userService = userService;
        this.loginService = loginService;
        this.qrService = qrService;
        this.exceptionHelper = exceptionHelper;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/register")
    @WithRateLimitProtection
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDTO registrationDTO, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        Map<String, Object> errorParams = responseHelper.getErrorParams(registrationDTO);
        if (!errorParams.isEmpty()) {
            return ResponseEntity.badRequest().body(errorParams);
        }
        try {
            userService.save(registrationDTO);
        } catch (MessagingException e) {
            errorParams.put("error", exceptionHelper.getLocalizedMsg("error.register.activationFailed"));
            return ResponseEntity.badRequest().body(errorParams);
        }
        return ResponseEntity.ok(Map.of("redirectUrl", "/activation/?target=" + registrationDTO.username()));
    }

    @GetMapping("/resend-activation/{email}")
    @PreAuthorize("isAnonymous()")
    @WithRateLimitProtection
    public ResponseEntity<?> resendActivation(@PathVariable String email) {
        try {
            userService.resendActivation(email);
            return responseHelper.redirectTo("/activation/?resend=true");
        } catch (LocalizedException | MessagingException e) {
            return responseHelper.redirectTo("/activation-error");
        }
    }

    @PostMapping("/resend-activation")
    @PreAuthorize("isAnonymous()")
    @WithRateLimitProtection
    public ResponseEntity<?> reactivateAccount(@RequestBody @Valid RecoveryInitDTO recoveryInitDTO, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        try {
            userService.resendActivation(recoveryInitDTO.username());
            return ResponseEntity.ok(Map.of("redirectUrl", "/activation/?resend=true"));
        } catch (LocalizedException | MessagingException e) {
            return responseHelper.redirectTo("/activation-error");
        }
    }

    @GetMapping("/register/{emailToken}")
    @WithRateLimitProtection
    public ResponseEntity<?> activate(@PathVariable String emailToken) {
        try {
            userService.activateAccount(emailToken);
            return responseHelper.redirectTo("/account-activated");
        } catch (LocalizedException e) {
            return responseHelper.redirectTo("/activation-error");
        }
    }

    @PostMapping("/recover")
    @WithRateLimitProtection
    public ResponseEntity<?> passwordRecovery(@RequestBody @Valid RecoveryInitDTO recoveryInitDTO, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        return responseHelper.getResponseEntity(recoveryInitDTO.username(), userService::sendPasswordRecovery);
    }

    @PostMapping("/confirm-recovery")
    @PreAuthorize("isAnonymous()")
    @WithRateLimitProtection
    public ResponseEntity<?> passwordRecovery(@RequestBody @Valid RecoveryDTO recovery, BindingResult br) {
        return userService.recoverPassword(recovery, br);
    }

    @PostMapping("/login")
    @WithRateLimitProtection
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response) throws LocalizedException {
        return loginService.handleLogin(authRequestDTO, response);
    }

    @GetMapping("/scan/{restaurantToken}")
    public ResponseEntity<?> scanQr(HttpServletResponse response, @PathVariable String restaurantToken) throws IOException {
        return qrService.scanQRCode(response, restaurantToken);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        String invalidatedJwtCookie = invalidateJwtCookie();
        response.setHeader("Set-Cookie", invalidatedJwtCookie);
        return ResponseEntity.ok().body(Map.of("redirectUrl", "/"));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/restaurant")
    public ResponseEntity<?> switchRestaurant(@RequestBody Long restaurantId) {
        ThrowingSupplier<User> userSupplier = userService::getCurrentUser;
        return responseHelper.buildResponse(restaurantId, userSupplier, userService::switchRestaurant);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/menu")
    public ResponseEntity<?> switchMenu(@RequestBody Long menuId) {
        ThrowingSupplier<User> userSupplier = userService::getCurrentUser;
        return responseHelper.buildResponse(menuId, userSupplier, userService::switchMenu);
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

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}