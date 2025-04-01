package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
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
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cms/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final ResponseHelper responseHelper;

    public CategoryController(CategoryService categoryService, UserService userService, ResponseHelper responseHelper) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll() {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            return ResponseEntity.ok(categoryService.findAll(activeMenuId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @GetMapping("/display-orders")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> getDisplayOrders() {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            return ResponseEntity.ok(categoryService.findAllDisplayOrders(activeMenuId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @PatchMapping("/display-orders")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> updateDisplayOrders(@RequestBody List<CategoryFormDTO> categories) {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            categoryService.updateDisplayOrders(categories, activeMenuId);
            return ResponseEntity.ok().build();
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> count() {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            return ResponseEntity.ok(categoryService.countAll(activeMenuId));
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAvailableAndVisible() {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            return ResponseEntity.ok(categoryService.findAllAvailableAndVisible(activeMenuId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @PostMapping("/show")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, categoryService::findById);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@Valid @RequestBody CategoryFormDTO category, BindingResult br) {
        return responseHelper.buildResponse(category, br, userService::getActiveMenuId, categoryService::save);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody CategoryFormDTO category, BindingResult br) {
        return responseHelper.buildResponse(category, br, userService::getActiveMenuId, categoryService::update);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> delete(@RequestBody Long id) {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            return ResponseEntity.ok(categoryService.delete(id, activeMenuId));
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