package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
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
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, userService::findById);
    }

    @GetMapping("/add")
    public ResponseEntity<User> add() {
        return ResponseEntity.ok(new User());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody User user, BindingResult br) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        } else if (userService.existsByUsername(user.getUsername())) {
            return badRequestWithParam("usernameExists");
        } else if (userService.existsByEmail(user.getEmail())) {
            return badRequestWithParam("emailExists");
        } else if (!user.getPassword().equals(user.getRepeatedPassword())) {
            return badRequestWithParam("passwordsNotMatch");
        }
        userService.save(user);
        return ResponseEntity.ok(params);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult br) throws LocalizedException {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        if (userService.isUpdatedUserValid(user)) {
            userService.save(user);
            return ResponseEntity.ok(params);
        } else {
            return badRequestWithParam(userService.getErrorParam(user));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody User user, Principal principal) {
        Map<String, Object> params = new HashMap<>();
        User currentAdmin = userService.findByUsername(principal.getName());
        if (currentAdmin.getId().equals(user.getId())) {
            params.put("illegalRemoval", true);
            return ResponseEntity.badRequest().body(params);
        }
        return responseHelper.buildResponse(user.getId(), userService::delete);
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