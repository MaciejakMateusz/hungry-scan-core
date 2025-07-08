package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.FileProcessingService;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/cms/qr")
@RequiredArgsConstructor
public class QrController {

    private final RestaurantTableService restaurantTableService;
    private final FileProcessingService fileProcessingService;
    private final UserService userService;
    private final QRService qrService;
    private final ResponseHelper responseHelper;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> list() {
        List<File> qrCodes = fileProcessingService.fileList();
        return ResponseEntity.ok(qrCodes);
    }

    @PostMapping("/tables/generate-qr")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> generateQr(@RequestBody Long id) {
        try {
            RestaurantTable restaurantTable = restaurantTableService.generateNewToken(id);
            qrService.generate(restaurantTable, "");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping("/generate-qr")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> generateBasicQr() {
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            qrService.generate(restaurantId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @GetMapping(value = "/download")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> downloadBasicQr() {
        try {
            Long restaurantId = userService.getActiveRestaurantId();
            return qrService.downloadQr(restaurantId);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> delete(@RequestBody String qrName) throws LocalizedException {
        boolean isDeleted = fileProcessingService.removeFile(qrName);
        return ResponseEntity.ok(isDeleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, DELETE, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}