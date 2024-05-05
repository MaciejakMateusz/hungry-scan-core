package pl.rarytas.hungry_scan_core.service.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface FileProcessingService {
    List<String> fileList();

    String uploadFile(String fileName, MultipartFile multipartFile);

    Resource downloadFile(String fileName) throws LocalizedException;
}
