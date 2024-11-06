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

    @PostMapping("/save-all")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> saveAll(@RequestBody List<Translatable> translatables) {
        translatableService.saveAll(translatables);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/translate")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<String> translate(@RequestBody Map<String, Object> requestData) {
        String response = translatableService.translate(requestData);
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}