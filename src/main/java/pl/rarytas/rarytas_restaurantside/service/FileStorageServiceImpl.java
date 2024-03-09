package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.service.interfaces.FileStorageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void storeFile(MultipartFile file) throws IOException {
        Path directoryPath = Paths.get(uploadDir);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        Path filePath = Paths.get(uploadDir + File.separator + file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);
    }
}