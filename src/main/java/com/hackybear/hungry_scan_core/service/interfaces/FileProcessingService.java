package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileProcessingService {

    List<File> fileList();

    boolean uploadFile(MultipartFile file) throws IOException;

    Resource downloadFile(String fileName) throws LocalizedException;

    boolean removeFile(String path) throws LocalizedException;
}
