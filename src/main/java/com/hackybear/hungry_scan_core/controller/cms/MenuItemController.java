package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final UserService userService;
    private final ResponseHelper responseHelper;

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, menuItemService::findById);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@RequestPart("menuItem") @Valid MenuItemFormDTO menuItem,
                                 BindingResult br,
                                 @RequestPart(value = "image", required = false) MultipartFile image) {
        if (br.hasErrors()) return responseHelper.createErrorResponse(br);
        try {
            Long activeMenuId = userService.getActiveMenuId();
            menuItemService.save(menuItem, activeMenuId, image);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> update(@RequestPart("menuItem") @Valid MenuItemFormDTO menuItem,
                                    BindingResult br,
                                    @RequestPart(value = "image", required = false) MultipartFile image) {
        if (br.hasErrors()) return responseHelper.createErrorResponse(br);
        try {
            Long activeMenuId = userService.getActiveMenuId();
            menuItemService.update(menuItem, activeMenuId, image);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PatchMapping(value = "/display-orders")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> updateDisplayOrders(@RequestBody List<MenuItemSimpleDTO> menuItems) {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            menuItemService.updateDisplayOrders(menuItems, activeMenuId);
            return ResponseEntity.ok().build();
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
    public ResponseEntity<?> delete(@RequestBody Long id) {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            menuItemService.delete(id, activeMenuId);
            return ResponseEntity.ok().build();
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