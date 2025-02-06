package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/items")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final UserService userService;
    private final ResponseHelper responseHelper;

    public MenuItemController(MenuItemService menuItemService, UserService userService,
                              ResponseHelper responseHelper) {
        this.menuItemService = menuItemService;
        this.userService = userService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, menuItemService::findById);
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@RequestBody @Valid MenuItemFormDTO menuItem, BindingResult br) {
        return responseHelper.buildResponse(menuItem, br, userService::getActiveMenuId, menuItemService::save);
    }

    @PatchMapping(value = "/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> update(@RequestBody @Valid MenuItemFormDTO menuItem, BindingResult br) {
        return responseHelper.buildResponse(menuItem, br, userService::getActiveMenuId, menuItemService::update);
    }

    @PatchMapping(value = "/display-orders")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> updateDisplayOrders(@RequestBody List<MenuItemSimpleDTO> menuItems) {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            return ResponseEntity.ok(menuItemService.updateDisplayOrders(menuItems, activeMenuId));
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> filterByName(@RequestBody String value) {
        return ResponseEntity.ok(menuItemService.filterByName(value));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> delete(@RequestBody MenuItemSimpleDTO menuItemDTO) {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            return ResponseEntity.ok(menuItemService.delete(menuItemDTO, activeMenuId));
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/view-event")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> persistViewEvent(@RequestBody Long menuItemId) {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            menuItemService.persistViewEvent(menuItemId, activeMenuId);
            return ResponseEntity.ok().build();
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