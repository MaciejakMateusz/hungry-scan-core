package com.hackybear.hungry_scan_core.record;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public record InMemoryMultipartFile(@NonNull String name,
                                    String originalFilename,
                                    String contentType,
                                    @NonNull byte[] bytes) implements MultipartFile {

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    @NonNull
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    @NonNull
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(@NonNull File dest) throws IOException {
        try (OutputStream os = new FileOutputStream(dest)) {
            os.write(bytes);
        }
    }

}