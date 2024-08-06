package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.service.interfaces.VariantService;
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
@RequestMapping("/api/cms/variants")
public class VariantController {

    private final VariantService variantService;
    private final ResponseHelper responseHelper;

    public VariantController(VariantService variantService, ResponseHelper responseHelper) {
        this.variantService = variantService;
        this.responseHelper = responseHelper;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, variantService::findById);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<Variant>> findAll() {
        List<Variant> variants = variantService.findAll();
        return ResponseEntity.ok(variants);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/item")
    public ResponseEntity<List<Variant>> findAllByMenuItem(@RequestBody Integer id) {
        List<Variant> variants = variantService.findAllByMenuItemId(id);
        return ResponseEntity.ok(variants);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> persist(@RequestBody @Valid Variant variant, BindingResult br) {
        return responseHelper.buildResponse(variant, br, variantService::save);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, variantService::delete);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
