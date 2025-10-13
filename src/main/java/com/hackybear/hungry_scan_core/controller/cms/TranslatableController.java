package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.TranslatableService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.hackybear.hungry_scan_core.utility.Fields.ROLES_EXCEPT_CUSTOMER;

@RestController
@RequestMapping("/api/cms/translatable")
@RequiredArgsConstructor
public class TranslatableController {

    private final TranslatableService translatableService;
    private final UserService userService;
    private final ResponseHelper responseHelper;

    @PostMapping("/save-all")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
    public ResponseEntity<?> saveAll(@RequestBody List<Translatable> translatables) {
        try {
            Long activeMenuId = userService.getActiveMenuId();
            Long activeRestaurantId = userService.getActiveRestaurantId();
            translatableService.saveAll(translatables, activeMenuId, activeRestaurantId);
            return ResponseEntity.ok().build();
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/translate")
    @PreAuthorize(ROLES_EXCEPT_CUSTOMER)
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