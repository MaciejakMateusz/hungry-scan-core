package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.SettingsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@PreAuthorize("hasAnyRole('ADMIN')")
public class SettingsController {

    private final SettingsService settingsService;
    private final ResponseHelper responseHelper;

    public SettingsController(SettingsService settingsService, ResponseHelper responseHelper) {
        this.settingsService = settingsService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    public ResponseEntity<?> getSettings() {
        try {
            return ResponseEntity.ok(settingsService.getSettings());
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateSettings(@RequestBody @Valid SettingsDTO settingsDTO, BindingResult br) {
        return responseHelper.buildResponse(settingsDTO, br, settingsService::save);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
