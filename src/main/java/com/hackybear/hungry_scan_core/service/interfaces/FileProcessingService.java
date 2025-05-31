package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

public interface FileProcessingService {

    List<File> fileList();

    Resource downloadFile(String fileName) throws LocalizedException;

    boolean removeFile(String path) throws LocalizedException;

}
