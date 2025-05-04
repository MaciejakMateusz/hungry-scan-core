package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.web.multipart.MultipartFile;


public interface S3Service {

    void uploadFile(Long menuItemId, MultipartFile file) throws LocalizedException;

    void deleteFile(Long menuItemId);

    String getPublicUrl(Long menuItemId);
}
