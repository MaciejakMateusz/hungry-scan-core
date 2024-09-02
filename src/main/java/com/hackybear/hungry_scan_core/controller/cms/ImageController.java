package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.service.interfaces.FileProcessingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cms/images")
public class ImageController {

    private final FileProcessingService fileProcessingService;
    private final ResponseHelper responseHelper;

    public ImageController(FileProcessingService fileProcessingService, ResponseHelper responseHelper) {
        this.fileProcessingService = fileProcessingService;
        this.responseHelper = responseHelper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> uploadImage(@RequestBody MultipartFile file) {
        try {
            fileProcessingService.uploadFile(file);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}