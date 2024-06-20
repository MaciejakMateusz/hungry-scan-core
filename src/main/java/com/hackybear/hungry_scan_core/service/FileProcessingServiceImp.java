package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.FileProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileProcessingServiceImp implements FileProcessingService {

    @Value("${QR_PATH}")
    private String basePath;

    @Value("${IMAGE_PATH}")
    private String imagePath;

    private final ExceptionHelper exceptionHelper;

    public FileProcessingServiceImp(ExceptionHelper exceptionHelper) {
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<String> fileList() {
        File dir = new File(basePath);
        File[] files = dir.listFiles();

        return files != null ? Arrays.stream(files).map(File::getName).collect(Collectors.toList()) : null;
    }

    @Override
    public void uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        File destFile = new File(imagePath, fileName);
        destFile.getParentFile().mkdirs();
        file.transferTo(destFile);
    }

    @Override
    public Resource downloadFile(String path) throws LocalizedException {
        String fullPath = basePath + path;
        File dir = new File(fullPath);
        if (!dir.exists()) {
            exceptionHelper.throwLocalizedMessage("error.fileProcessingService.fileNotFound", fullPath);
        }

        try {
            UrlResource resource = new UrlResource(dir.toURI());
            if (!resource.exists() || !resource.isReadable()) {
                throw new LocalizedException("error.fileProcessingService.downloadError");
            }
            return resource;
        } catch (Exception e) {
            throw new LocalizedException("error.fileProcessingService.downloadError");
        }
    }

}