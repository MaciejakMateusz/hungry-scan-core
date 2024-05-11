package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileProcessingService {
    List<String> fileList();

    String uploadFile(String fileName, MultipartFile multipartFile);

    Resource downloadFile(String fileName) throws LocalizedException;
}
