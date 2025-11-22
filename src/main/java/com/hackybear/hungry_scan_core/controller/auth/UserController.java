package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.annotation.WithRateLimitProtection;
import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.LoginService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

import static com.hackybear.hungry_scan_core.utility.Fields.ROLES_EXCEPT_CUSTOMER;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginService loginService;
    private final ResponseHelper responseHelper;

    @GetMapping("/profile")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<?> getUserProfileData() {
        try {
            User user = userService.getCurrentUser();
            return ResponseEntity.ok(userService.getUserProfileData(user));
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PatchMapping("/profile")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    @WithRateLimitProtection
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody final UserProfileUpdateDTO dto, BindingResult br) {
        try {
            User user = userService.getCurrentUser();
            return userService.updateUserProfile(user, dto, br);
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/register")
    @WithRateLimitProtection
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDTO registrationDTO, BindingResult br) {
        return userService.save(registrationDTO, br);
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, Principal principal) {
        return loginService.handleLogout(response, principal.getName());
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/restaurant")
    public ResponseEntity<?> switchRestaurant(@RequestBody Long restaurantId) {
        ThrowingSupplier<User> userSupplier = userService::getCurrentUser;
        return responseHelper.buildResponse(restaurantId, userSupplier, userService::switchRestaurant);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current-restaurant")
    public ResponseEntity<?> getCurrentRestaurant() {
        return responseHelper.buildResponse(userService::getCurrentRestaurant);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/menu")
    public ResponseEntity<?> switchMenu(@RequestBody Long menuId) {
        ThrowingSupplier<User> userSupplier = userService::getCurrentUser;
        return responseHelper.buildResponse(menuId, userSupplier, userService::switchMenu);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current-menu")
    public ResponseEntity<?> getCurrentMenu() {
        return responseHelper.buildResponse(userService::getCurrentMenu);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}