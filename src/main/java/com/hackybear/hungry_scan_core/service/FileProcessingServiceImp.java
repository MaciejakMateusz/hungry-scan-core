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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileProcessingServiceImp implements FileProcessingService {

    @Value("${QR_PATH}")
    private String basePath;

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
    public String uploadFile(String fileName, MultipartFile multipartFile) {
        File dir = new File(basePath + fileName);

        if (dir.exists()) {
            return "EXIST";
        }

        Path path = Path.of(basePath + fileName);

        try {
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return "CREATED";
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "FAILED";
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