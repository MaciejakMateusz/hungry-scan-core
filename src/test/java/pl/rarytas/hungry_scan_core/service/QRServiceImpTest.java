package pl.rarytas.hungry_scan_core.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import pl.rarytas.hungry_scan_core.entity.RestaurantTable;
import pl.rarytas.hungry_scan_core.service.interfaces.QRService;
import pl.rarytas.hungry_scan_core.service.interfaces.RestaurantTableService;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QRServiceImpTest {

    @Autowired
    private QRService qrService;

    @Value("${QR_PATH}")
    private String directory;

    @Autowired
    RestaurantTableService restaurantTableService;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    void shouldGenerate() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(1);
        qrService.generate(restaurantTable);
        String qrName = restaurantTable.getQrName();
        File file = new File(directory + qrName);
        assertEquals("QR code - Table number 1, Table ID 1.png", file.getName());
    }
}