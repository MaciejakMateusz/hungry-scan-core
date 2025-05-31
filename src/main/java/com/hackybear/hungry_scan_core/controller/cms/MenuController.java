package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.MenuService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cms/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final UserService userService;
    private final ResponseHelper responseHelper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getAll() {
        try {
            Long activeRestaurantId = userService.getActiveRestaurantId();
            return ResponseEntity.ok(menuService.findAll(activeRestaurantId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @PostMapping("/show")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> show(@RequestBody Long id) {
        ThrowingSupplier<Long> activeRestaurantIdProvider = userService::getActiveRestaurantId;
        return responseHelper.buildResponse(id, activeRestaurantIdProvider, menuService::findById);
    }

    @GetMapping("/customer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> projectPlannedMenu() {
        try {
            Long menuId = userService.getActiveMenuId();
            return ResponseEntity.ok(menuService.projectPlannedMenu(menuId));
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@Valid @RequestBody MenuSimpleDTO menuDTO, BindingResult br) {
        return responseHelper.buildResponse(menuDTO, br, userService::getCurrentUser, menuService::save);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody MenuSimpleDTO menuDTO, BindingResult br) {
        return responseHelper.buildResponse(menuDTO, br, userService::getActiveRestaurantId, menuService::update);
    }

    @PatchMapping("/update-plans")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> updatePlans(@Valid @RequestBody List<MenuSimpleDTO> menus, BindingResult br) {
        return responseHelper.buildResponse(menus, br, userService::getActiveRestaurantId, menuService::updatePlans);
    }

    @PatchMapping("/switch-standard")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> switchStandard() {
        try {
            User currentUser = userService.getCurrentUser();
            menuService.switchStandard(currentUser);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> delete() {
        try {
            User currentUser = userService.getCurrentUser();
            return responseHelper.buildResponse(currentUser, menuService::delete);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PatchMapping("/duplicate")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> duplicate() {
        try {
            User currentUser = userService.getCurrentUser();
            return responseHelper.buildResponse(currentUser, menuService::duplicate);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}