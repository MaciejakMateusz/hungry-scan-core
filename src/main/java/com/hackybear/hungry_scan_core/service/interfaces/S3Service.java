package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface S3Service {

    void uploadFile(String path, Long id, MultipartFile file) throws LocalizedException;

    void deleteFile(String path, Long menuItemId);

    void deleteAllFiles(String path, List<Long> menuItemIds);

    String getPublicUrl(String path, Long menuItemId);

    ResponseEntity<Resource> downloadFile(String path, Long id);

    ResponseEntity<Resource> downloadFile(String path, Long restaurantId, Long tableId);
}
