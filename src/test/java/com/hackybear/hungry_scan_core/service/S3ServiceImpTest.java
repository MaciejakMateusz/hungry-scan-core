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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

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

    private static final String S3_PATH = "local/menuItems";

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

        service.uploadFile(S3_PATH, menuItemId, file);

        ArgumentCaptor<PutObjectRequest> reqCap = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCap = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(reqCap.capture(), bodyCap.capture());

        PutObjectRequest req = reqCap.getValue();
        assertEquals("test-bucket", req.bucket());
        assertEquals("local/menuItems/7.png", req.key());
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
                () -> service.uploadFile(S3_PATH, 2L, file),
                "IOException during getInputStream() should trigger a LocalizedException");
    }

    @Test
    void testUploadFile_keyPath_overload_success() throws Exception {
        String keyPath = "free/standing/key.png";
        byte[] content = {1, 2, 3};
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        when(file.getSize()).thenReturn((long) content.length);

        service.uploadFile(keyPath, file);

        ArgumentCaptor<PutObjectRequest> reqCap = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(reqCap.capture(), any(RequestBody.class));

        PutObjectRequest req = reqCap.getValue();
        assertEquals("test-bucket", req.bucket());
        assertEquals(keyPath, req.key());
        assertEquals("image/png", req.contentType());
    }

    @Test
    void testDeleteFile_invokesDeleteObject() {
        long menuItemId = 99L;

        service.deleteFile(S3_PATH, menuItemId);

        ArgumentCaptor<DeleteObjectRequest> cap = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(cap.capture());

        DeleteObjectRequest req = cap.getValue();
        assertEquals("test-bucket", req.bucket());
        assertEquals("local/menuItems/99.png", req.key());
    }

    @Test
    void testDeleteAllFiles_invokesBulkDelete() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        service.deleteAllFiles(S3_PATH, ids);

        ArgumentCaptor<DeleteObjectsRequest> cap = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(s3Client).deleteObjects(cap.capture());

        DeleteObjectsRequest req = cap.getValue();
        assertEquals("test-bucket", req.bucket(), "Bucket name should match");

        List<String> keys = req.delete().objects().stream()
                .map(ObjectIdentifier::key)
                .toList();

        assertEquals(3, keys.size(), "Should have three objects to delete");
        assertTrue(keys.containsAll(
                Arrays.asList("local/menuItems/1.png", "local/menuItems/2.png", "local/menuItems/3.png")
        ), "All requested keys must be present");
    }

    @Test
    void testGetPublicUrl_returnsCorrectlyConcatenatedUrl() {
        long menuItemId = 5L;

        String url = service.getPublicUrl(S3_PATH, menuItemId);

        assertEquals(
                "http://test-bucket-url/local/menuItems/5.png",
                url,
                "getPublicUrl should prepend bucketUrl and folder"
        );
    }

    @Test
    void testDownloadFile_byKeyPath_success() throws IOException {
        String keyPath = "foo/bar/baz.png";
        byte[] bytes = "PNG".getBytes();

        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> stream = mock(ResponseInputStream.class);
        when(stream.readAllBytes()).thenReturn(bytes);
        when(stream.response()).thenReturn(GetObjectResponse.builder().contentLength((long) bytes.length).build());
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        ResponseEntity<Resource> resp = service.downloadFile(keyPath);

        ArgumentCaptor<GetObjectRequest> cap = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(cap.capture());
        assertEquals("test-bucket", cap.getValue().bucket());
        assertEquals(keyPath, cap.getValue().key());

        assertEquals(200, resp.getStatusCode().value());
        assertEquals(MediaType.IMAGE_PNG, resp.getHeaders().getContentType());
        assertEquals(bytes.length, resp.getHeaders().getContentLength());

        String cd = resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(cd);
        assertTrue(cd.contains("HungryScan - QR Code.png"));
    }

    @Test
    void testDownloadFile_byPathAndId_success() throws IOException {
        long id = 42L;
        byte[] bytes = new byte[]{9, 8, 7};

        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> stream = mock(ResponseInputStream.class);
        when(stream.readAllBytes()).thenReturn(bytes);
        when(stream.response()).thenReturn(GetObjectResponse.builder().contentLength((long) bytes.length).build());
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        ResponseEntity<Resource> resp = service.downloadFile(S3_PATH, id);

        ArgumentCaptor<GetObjectRequest> cap = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(cap.capture());
        assertEquals("test-bucket", cap.getValue().bucket());
        assertEquals("local/menuItems/42.png", cap.getValue().key());

        assertEquals(200, resp.getStatusCode().value());
        assertEquals(MediaType.IMAGE_PNG, resp.getHeaders().getContentType());
        assertEquals(bytes.length, resp.getHeaders().getContentLength());
        String cd = resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(cd);
        assertTrue(cd.contains("HungryScan - QR Code.png"));
    }

    @Test
    void testDownloadFile_restaurantTable_success_customFilename() throws IOException {
        long restaurantId = 11L;
        long tableId = 22L;
        byte[] bytes = new byte[]{1, 2, 3, 4};

        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> stream = mock(ResponseInputStream.class);
        when(stream.readAllBytes()).thenReturn(bytes);
        when(stream.response()).thenReturn(GetObjectResponse.builder().contentLength((long) bytes.length).build());
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        ResponseEntity<Resource> resp = service.downloadFile("tables", restaurantId, tableId);

        ArgumentCaptor<GetObjectRequest> cap = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(cap.capture());
        assertEquals("test-bucket", cap.getValue().bucket());
        assertEquals("tables/11/22.png", cap.getValue().key());

        assertEquals(200, resp.getStatusCode().value());
        String cd = resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(cd);
        assertTrue(cd.contains("22.png"), "Should include the table id in the filename");
    }

    @Test
    void testDownloadFile_ioError_returns500() throws IOException {
        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> stream = mock(ResponseInputStream.class);
        when(stream.readAllBytes()).thenThrow(new IOException("read fail"));
        when(stream.response()).thenReturn(GetObjectResponse.builder().contentLength(0L).build());
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        ResponseEntity<Resource> resp = service.downloadFile("any/key.png");

        assertEquals(500, resp.getStatusCode().value(), "Should return 500 on IOException while reading");
    }


    @Test
    void testCopyFile_sourceMissing_doesNothing() {
        S3Exception notFound = mock(S3Exception.class);
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(notFound);

        service.copyFile(S3_PATH, 1L, 2L);

        verify(s3Client, never()).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    void testCopyFile_sourceExists_copies() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        service.copyFile(S3_PATH, 10L, 20L);

        ArgumentCaptor<CopyObjectRequest> cap = ArgumentCaptor.forClass(CopyObjectRequest.class);
        verify(s3Client).copyObject(cap.capture());

        CopyObjectRequest req = cap.getValue();
        assertEquals("test-bucket", req.sourceBucket());
        assertEquals("local/menuItems/10.png", req.sourceKey());
        assertEquals("test-bucket", req.destinationBucket());
        assertEquals("local/menuItems/20.png", req.destinationKey());
        assertEquals(MetadataDirective.COPY, req.metadataDirective());
    }

    @Test
    void testCopyFile_sourceExists_s3Throws_isSwallowed() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());

        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenThrow(S3Exception.builder().statusCode(500).message("boom").build());

        assertDoesNotThrow(() -> service.copyFile(S3_PATH, 3L, 4L));
        verify(s3Client).copyObject(any(CopyObjectRequest.class));
    }
}
