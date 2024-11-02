package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.VariantDTO;
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
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, variantService::findById);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/item")
    public ResponseEntity<List<VariantDTO>> findAllByMenuItem(@RequestBody Long id) {
        return ResponseEntity.ok(variantService.findAllByMenuItemId(id));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> persist(@RequestBody @Valid VariantDTO variant, BindingResult br) {
        return responseHelper.buildResponse(variant, br, variantService::save);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody @Valid VariantDTO variant, BindingResult br) {
        return responseHelper.buildResponse(variant, br, variantService::update);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, variantService::delete);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
