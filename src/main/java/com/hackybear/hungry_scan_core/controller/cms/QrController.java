package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.FileProcessingService;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cms/qr")
public class QrController {

    private final RestaurantTableService restaurantTableService;
    private final FileProcessingService fileProcessingService;
    private final QRService qrService;
    private final ResponseHelper responseHelper;

    public QrController(RestaurantTableService restaurantTableService,
                        FileProcessingService fileProcessingService,
                        QRService qrService,
                        ResponseHelper responseHelper) {
        this.restaurantTableService = restaurantTableService;
        this.fileProcessingService = fileProcessingService;
        this.qrService = qrService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/tables/generate-qr")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> generateQr(@RequestBody Integer id) {
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
            qrService.generate();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PostMapping(value = "/download")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> downloadBasicQr(@RequestBody String qrPath) {
        try {
            Resource file = fileProcessingService.downloadFile(qrPath);

            String filename = file.getFilename();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok().headers(headers).body(file);
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