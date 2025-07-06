package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileProcessingServiceImp implements FileProcessingService {

    @Value("${qr.path}")
    private String qrPath;

    private final ExceptionHelper exceptionHelper;

    @Override
    public List<File> fileList() {
        File dir = new File(qrPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles();

        return files != null ? Arrays.stream(files).collect(Collectors.toList()) : new ArrayList<>();
    }

    @Override
    public Resource downloadFile(String path) throws LocalizedException {
        String fullPath = qrPath + path;
        File dir = new File(fullPath);
        if (!dir.exists()) {
            exceptionHelper.throwLocalizedMessage("error.fileProcessingService.fileNotFound", fullPath);
        }

        return executeDownload(dir);
    }

    @Override
    public boolean removeFile(String path) throws LocalizedException {
        String fullPath = qrPath + path;
        File dir = new File(fullPath);
        if (!dir.exists()) {
            exceptionHelper.throwLocalizedMessage("error.fileProcessingService.fileNotFound", fullPath);
        }

        return executeDeletion(dir);
    }

    private UrlResource executeDownload(File dir) throws LocalizedException {
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

    private boolean executeDeletion(File dir) throws LocalizedException {
        boolean isDeleted;
        try {
            isDeleted = dir.delete();
        } catch (Exception e) {
            throw new LocalizedException("error.fileProcessingService.deleteError");
        }
        return isDeleted;
    }
}