package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.IngredientSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.IngredientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cms/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;
    private final ResponseHelper responseHelper;

    public IngredientController(IngredientService ingredientService, ResponseHelper responseHelper) {
        this.ingredientService = ingredientService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(ingredientService.findAll());
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> pages(@RequestBody Map<String, Object> params) {
        Integer pageSize = (Integer) params.get("pageSize");
        Integer pageNumber = (Integer) params.get("pageNumber");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        try {
            return ResponseEntity.ok(ingredientService.findAllPages(pageable));
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }

    }

    @PostMapping("/show")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, ingredientService::findById);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> add(@RequestBody @Valid IngredientSimpleDTO ingredient,
                                 BindingResult br) {
        return responseHelper.buildResponse(ingredient, br, ingredientService::save);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> update(@RequestBody @Valid IngredientSimpleDTO ingredient,
                                    BindingResult br) {
        return responseHelper.buildResponse(ingredient, br, ingredientService::update);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Long id) {
        return responseHelper.buildResponse(id, ingredientService::delete);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> filterByName(@RequestBody String value) {
        try {
            return ResponseEntity.ok(ingredientService.filterByName(value));
        } catch (LocalizedException e) {
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