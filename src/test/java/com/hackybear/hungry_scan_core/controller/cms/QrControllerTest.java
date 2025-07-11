package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private MockMvc mockMvc;

    @Value("${qr.path}")
    private String qrPath;

    @MockitoBean
    private QRService qrService;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.doNothing().when(qrService).generate(Mockito.any());
    }

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
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
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
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Order(4)
    void shouldDownload() throws Exception {
        Mockito.doReturn(ResponseEntity.ok("QrCode")).when(qrService).downloadQr(Mockito.any());

        ResultActions resultActions = mockMvc.perform(get("/api/cms/qr/download")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertEquals("QrCode", responseBody);
    }

}