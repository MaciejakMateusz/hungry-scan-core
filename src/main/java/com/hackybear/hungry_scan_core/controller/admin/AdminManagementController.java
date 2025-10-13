package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.UserDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.RoleService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminManagementController {

    private final UserService userService;
    private final RoleService roleService;
    private final ResponseHelper responseHelper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> users(Principal principal) {
        try {
            return ResponseEntity.ok(userService.findAll(principal.getName()));
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> roles() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findUser(@RequestBody String username) {
        try {
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(userService.getUserProfileData(user));
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> add(@Valid @RequestBody UserDTO userDTO,
                                 BindingResult br,
                                 Principal principal) {
        return userService.addToOrganization(userDTO, br, principal.getName());
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> update(@Valid @RequestBody UserDTO userDTO, BindingResult br, Principal principal) throws LocalizedException {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(responseHelper.getFieldErrors(br));
        }
        return userService.update(userDTO, principal.getName());
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> delete(@RequestBody String username, Principal principal) {
        return userService.delete(username, principal.getName());
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

}