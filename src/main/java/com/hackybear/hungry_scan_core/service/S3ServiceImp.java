package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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

    private static final Logger log = LoggerFactory.getLogger(S3ServiceImp.class);
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
        String key = getKey(path, id);

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
    public void uploadFile(String keyPath, MultipartFile file) throws LocalizedException {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyPath)
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
        String key = getKey(path, id);
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
    public ResponseEntity<Resource> downloadFile(String keyPath) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyPath)
                .build();

        String headerValue = "attachment; filename=\"HungryScan - QR Code.png\"; " +
                "filename*=UTF-8''HungryScan%20-%20QR%20Code.png";
        return getResourceResponseEntity(headerValue, request);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String path, Long id) {
        String key = getKey(path, id);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        String headerValue = "attachment; filename=\"HungryScan - QR Code.png\"; " +
                "filename*=UTF-8''HungryScan%20-%20QR%20Code.png";
        return getResourceResponseEntity(headerValue, request);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String path, Long restaurantId, Long tableId) {
        String key = path + "/" + restaurantId + "/" + tableId + ".png";
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        String headerValue = "attachment; filename=\"HungryScan - QR Code - " + tableId + ".png\"; " +
                "filename*=UTF-8''HungryScan%20-%20QR%20Code - " + tableId + ".png";
        return getResourceResponseEntity(headerValue, request);
    }

    @Override
    public void copyFile(String path, Long sourceId, Long destinationId) {
        if (!exists(path, sourceId)) return;

        String sourceKey = getKey(path, sourceId);
        String destKey = getKey(path, destinationId);

        try {
            s3.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(destKey)
                    .metadataDirective(MetadataDirective.COPY)
                    .build());
        } catch (S3Exception e) {
            log.warn("S3 copy failed for {} -> {}: {}", sourceKey, destKey, e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage());
        }
    }

    private boolean exists(String path, Long id) {
        try {
            s3.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(getKey(path, id))
                    .build());
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    private String getKey(String path, Long id) {
        return path + "/" + id + ".png";
    }

    private ResponseEntity<Resource> getResourceResponseEntity(String headerValue, GetObjectRequest request) {
        ResponseInputStream<GetObjectResponse> in = s3.getObject(request);
        try {
            byte[] bytes = in.readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .contentType(MediaType.IMAGE_PNG)
                    .contentLength(in.response().contentLength())
                    .body(new ByteArrayResource(bytes));
        } catch (IOException e) {
            log.error("Failed to read S3 object {} from bucket {}: {}", request.key(), request.bucket(), e, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
