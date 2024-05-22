package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.service.interfaces.TranslatableService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cms/translatable")
public class TranslatableController {

    private final TranslatableService translatableService;


    public TranslatableController(TranslatableService translatableService) {
        this.translatableService = translatableService;
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Translatable>> allFromCategories() {
        return ResponseEntity.ok(translatableService.findAllFromCategories());
    }

    @GetMapping("/menu-items")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Object[]>> allFromMenuItems() {
        return ResponseEntity.ok(translatableService.findAllFromMenuItems());
    }

    @GetMapping("/variants")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Translatable>> allFromVariants() {
        return ResponseEntity.ok(translatableService.findAllFromCategories());
    }

    @GetMapping("/ingredients")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<Translatable>> allFromIngredients() {
        return ResponseEntity.ok(translatableService.findAllFromCategories());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> show(@RequestBody List<Translatable> translatables) {
        translatableService.saveAllNames(translatables);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}