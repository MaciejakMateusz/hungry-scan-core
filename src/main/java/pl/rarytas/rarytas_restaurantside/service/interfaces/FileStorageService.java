package pl.rarytas.rarytas_restaurantside.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile multipartFile) throws IOException;
}
