package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface S3Service {

    void uploadFile(Long menuItemId, MultipartFile file) throws LocalizedException;

    void deleteFile(Long menuItemId);

    void deleteAllFiles(List<Long> menuItemIds);

    String getPublicUrl(Long menuItemId);
}
