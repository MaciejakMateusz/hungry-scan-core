package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QrControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Value("${QR_PATH}")
    private String qrPath;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"ADMIN"})
    @Order(2)
    void shouldGenerateQr() throws Exception {
        RestaurantTable restaurantTable =
                apiRequestUtils.postObjectExpect200("/api/cms/tables/show", 6, RestaurantTable.class);
        assertEquals("59ebc00c-b580-4dff-9788-2df90b1d4bba", restaurantTable.getToken());

        apiRequestUtils.postAndExpect200("/api/cms/qr/tables/generate-qr", 6);

        restaurantTable =
                apiRequestUtils.postObjectExpect200("/api/cms/tables/show", 6, RestaurantTable.class);
        assertNotEquals("59ebc00c-b580-4dff-9788-2df90b1d4bba", restaurantTable.getToken());
        assertEquals(36, restaurantTable.getToken().length());

        String fileName = restaurantTable.getQrName();
        Path qrFilePath = Paths.get(qrPath, fileName);
        assertTrue(Files.exists(qrFilePath));

        File dir = new File(qrPath + fileName);
        assertTrue(dir.delete());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"ADMIN"})
    @Order(3)
    void shouldGenerateBasicQr() throws Exception {
        apiRequestUtils.simplePost("/api/cms/qr/generate-qr");
        Path qrFilePath = Paths.get(qrPath);
        assertTrue(Files.exists(qrFilePath) && Files.isDirectory(qrFilePath));

        try (Stream<Path> paths = Files.list(qrFilePath)) {
            paths.forEach(path -> {
                assertTrue(Files.exists(path));
                assertEquals("QR code - HungryScan", path.getFileName().toString().substring(0, 20));
            });
        }
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"MANAGER"})
    @Order(4)
    void shouldDownload() throws Exception {
        String fileName = getFirstQrName();
        Resource resource = apiRequestUtils.postAndFetchResource("/api/cms/qr/download", fileName);

        InputStream inputStream = resource.getInputStream();
        File file = new File("./src/test/files/download/Downloaded HungryScan QR.png");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            log.error("The test file could not be downloaded.", e);
        }

        assertEquals("Downloaded HungryScan QR.png", file.getName());
        assertTrue(file.delete());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"MANAGER"})
    @Order(5)
    void shouldDelete() throws Exception {
        String fileName = getFirstQrName();
        apiRequestUtils.deleteAndExpect200("/api/cms/qr", fileName);
        File file = new File("./src/test/files/qr/" + fileName);
        assertFalse(file.exists());
    }

    private String getFirstQrName() throws IOException {
        Path qrFilePath = Paths.get(qrPath);
        String fileName;
        try (Stream<Path> paths = Files.list(qrFilePath)) {
            Path file = paths.findFirst().orElseThrow();
            fileName = file.getFileName().toString();
        }
        return fileName;
    }
}