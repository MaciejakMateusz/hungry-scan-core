package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.MenuService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cms/menus")
public class MenuController {

    private final MenuService menuService;
    private final UserService userService;
    private final ResponseHelper responseHelper;

    public MenuController(MenuService menuService,
                          UserService userService,
                          ResponseHelper responseHelper) {
        this.menuService = menuService;
        this.userService = userService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll() {
        try {
            Long activeRestaurantId = userService.getActiveRestaurantId();
            return ResponseEntity.ok(menuService.findAll(activeRestaurantId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, menuService::findById);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@Valid @RequestBody MenuSimpleDTO menuDTO, BindingResult br) {
        return responseHelper.buildResponse(menuDTO, br, userService::getActiveRestaurantId, menuService::save);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody MenuSimpleDTO menuDTO, BindingResult br) {
        return responseHelper.buildResponse(menuDTO, br, userService::getActiveRestaurantId, menuService::update);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> delete(@RequestBody Long id) {
        try {
            Long activeRestaurantId = userService.getActiveRestaurantId();
            menuService.delete(id, activeRestaurantId);
            return ResponseEntity.ok().build();
        } catch (LocalizedException | AuthenticationException e) {
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