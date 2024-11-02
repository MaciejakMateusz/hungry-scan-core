package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {

    private final UserService userService;
    private final ResponseHelper responseHelper;

    public AdminManagementController(UserService userService,
                                     ResponseHelper responseHelper) {
        this.userService = userService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    public ResponseEntity<?> users() {
        try {
            return ResponseEntity.ok(userService.findAll());
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @GetMapping("/waiters")
    public ResponseEntity<List<User>> waiters() {
        List<User> users = userService.findAllByRole("ROLE_WAITER");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/cooks")
    public ResponseEntity<List<User>> cooks() {
        List<User> users = userService.findAllByRole("ROLE_COOK");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<User>> managers() {
        List<User> users = userService.findAllByRole("ROLE_MANAGER");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> admins() {
        List<User> users = userService.findAllByRole("ROLE_ADMIN");
        return ResponseEntity.ok(users);
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody String username) {
        return responseHelper.getResponseEntity(username, userService::findByUsername);
    }

    @GetMapping("/add")
    public ResponseEntity<User> add() {
        return ResponseEntity.ok(new User());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody RegistrationDTO registrationDTO, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        Map<String, Object> errorParams = responseHelper.getErrorParams(registrationDTO);
        if (!errorParams.isEmpty()) {
            return ResponseEntity.badRequest().body(errorParams);
        }
        try {
            userService.addToOrganization(registrationDTO);
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody RegistrationDTO registrationDTO, BindingResult br) throws LocalizedException {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        if (!userService.isUpdatedUserValid(registrationDTO)) {
            return badRequestWithParam(userService.getErrorParam(registrationDTO));
        }
        return responseHelper.getResponseEntity(registrationDTO, userService::update);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody String username, Principal principal) {
        Map<String, Object> params = new HashMap<>();
        if (principal.getName().equals(username)) {
            params.put("illegalRemoval", true);
            return ResponseEntity.badRequest().body(params);
        }
        return responseHelper.buildResponse(username, userService::delete);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> badRequestWithParam(String paramName) {
        Map<String, Object> params = new HashMap<>();
        params.put(paramName, true);
        return ResponseEntity.badRequest().body(params);
    }
}