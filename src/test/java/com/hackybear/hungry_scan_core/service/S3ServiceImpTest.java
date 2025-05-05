package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class S3ServiceImpTest {

    @Mock
    private ExceptionHelper exceptionHelper;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3ServiceImp service;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        this.mocks = MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(service, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(service, "bucketUrl", "http://test-bucket-url");
        ReflectionTestUtils.setField(service, "s3", s3Client);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (this.mocks != null) {
            this.mocks.close();
        }
    }

    @Test
    void testInit_createsS3Client() {
        ReflectionTestUtils.setField(service, "accessKey", "AKIA_TEST");
        ReflectionTestUtils.setField(service, "secretKey", "SECRET_TEST");
        ReflectionTestUtils.setField(service, "region", "us-west-2");

        service.init();

        Object constructed = ReflectionTestUtils.getField(service, "s3");
        try (S3Client s3 = assertInstanceOf(S3Client.class, constructed)) {
            assertNotNull(s3, "S3Client should have been initialized");
        }
    }

    @Test
    void testUploadFile_success() throws Exception {
        long menuItemId = 7L;
        byte[] content = "hello".getBytes();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("pic.png");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        when(file.getSize()).thenReturn((long) content.length);

        service.uploadFile(menuItemId, file);

        ArgumentCaptor<PutObjectRequest> reqCap = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCap = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(reqCap.capture(), bodyCap.capture());

        PutObjectRequest req = reqCap.getValue();
        assertEquals("test-bucket", req.bucket());
        assertEquals("menuItems/7", req.key());
        assertEquals("image/png", req.contentType());
    }

    @Test
    void testUploadFile_ioException_triggersLocalizedException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("error.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getInputStream()).thenThrow(new IOException("boom"));

        doThrow(new LocalizedException("upload failed"))
                .when(exceptionHelper).throwLocalizedMessage("error.s3Service.uploadingFailed");

        assertThrows(LocalizedException.class,
                () -> service.uploadFile(2L, file),
                "IOException during getInputStream() should trigger a LocalizedException");
    }

    @Test
    void testDeleteFile_invokesDeleteObject() {
        long menuItemId = 99L;

        service.deleteFile(menuItemId);

        ArgumentCaptor<DeleteObjectRequest> cap = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(cap.capture());

        DeleteObjectRequest req = cap.getValue();
        assertEquals("test-bucket", req.bucket());
        assertEquals("menuItems/99", req.key());
    }

    @Test
    void testDeleteAllFiles_invokesBulkDelete() {
        // given
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // when
        service.deleteAllFiles(ids);

        // then
        ArgumentCaptor<DeleteObjectsRequest> cap = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(s3Client).deleteObjects(cap.capture());

        DeleteObjectsRequest req = cap.getValue();
        assertEquals("test-bucket", req.bucket(), "Bucket name should match");

        // extract the list of keys we asked to delete
        List<String> keys = req.delete().objects().stream()
                .map(ObjectIdentifier::key)
                .toList();

        assertEquals(3, keys.size(), "Should have three objects to delete");
        assertTrue(keys.containsAll(
                Arrays.asList("menuItems/1", "menuItems/2", "menuItems/3")
        ), "All requested keys must be present");
    }

    @Test
    void testGetPublicUrl_returnsCorrectlyConcatenatedUrl() {
        long menuItemId = 5L;

        String url = service.getPublicUrl(menuItemId);

        assertEquals(
                "http://test-bucket-url/menuItems/5",
                url,
                "getPublicUrl should prepend bucketUrl and folder"
        );
    }
}
