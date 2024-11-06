package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.CategoryService;
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
    private final ResponseHelper responseHelper;

    public CategoryController(CategoryService categoryService, ResponseHelper responseHelper) {
        this.categoryService = categoryService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(categoryService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @GetMapping("/display-orders")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> getDisplayOrders() {
        try {
            return ResponseEntity.ok(categoryService.findAllDisplayOrders());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getStackTrace());
        }
    }

    @PatchMapping("/display-orders")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> updateDisplayOrders(@RequestBody List<CategoryFormDTO> categories) {
        try {
            return ResponseEntity.ok().body(categoryService.updateDisplayOrders(categories));
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> count() {
        try {
            return ResponseEntity.ok(categoryService.countAll());
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAvailableAndVisible() {
        try {
            return ResponseEntity.ok(categoryService.findAllAvailableAndVisible());
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
        return responseHelper.buildResponse(category, br, categoryService::save);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody CategoryFormDTO category, BindingResult br) {
        return responseHelper.buildResponse(category, br, categoryService::update);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> delete(@RequestBody Long id) {
        try {
            return ResponseEntity.ok(categoryService.delete(id));
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