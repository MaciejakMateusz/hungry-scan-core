package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3ServiceImp implements S3Service {

    private final ExceptionHelper exceptionHelper;

    private S3Client s3;

    @Value("${aws.accesskey}")
    private String accessKey;

    @Value("${aws.secretkey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${aws.bucket.url}")
    private String bucketUrl;

    @PostConstruct
    public void init() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Override
    public void uploadFile(String path, Long id, MultipartFile file) throws LocalizedException {
        String key = path + "/" + id;

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            exceptionHelper.throwLocalizedMessage("error.s3Service.uploadingFailed");
        }
    }

    @Override
    public void deleteFile(String path, Long id) {
        String key = path + "/" + id;
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.deleteObject(deleteRequest);
    }

    @Override
    public void deleteAllFiles(String path, List<Long> ids) {
        List<ObjectIdentifier> toDelete = ids.stream()
                .map(id -> ObjectIdentifier.builder()
                        .key(path + "/" + id)
                        .build())
                .collect(Collectors.toList());

        int chunkSize = 1000;
        for (int i = 0; i < toDelete.size(); i += chunkSize) {
            List<ObjectIdentifier> chunk = toDelete.subList(
                    i,
                    Math.min(i + chunkSize, toDelete.size())
            );

            Delete delete = Delete.builder()
                    .objects(chunk)
                    .build();

            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(delete)
                    .build();

            s3.deleteObjects(deleteRequest);
        }
    }

    @Override
    public String getPublicUrl(String path, Long id) {
        return bucketUrl + "/" + path + "/" + id;
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String path, Long id) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(path + "/" + id)
                .build();
        ResponseInputStream<GetObjectResponse> s3Stream = s3.getObject(req);
        GetObjectResponse metadata = s3Stream.response();

        InputStreamResource resource = new InputStreamResource(s3Stream);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.contentType()))
                .contentLength(metadata.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + id + "\"")
                .body(resource);
    }
}
