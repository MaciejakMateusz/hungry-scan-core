package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.service.interfaces.QRService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QRServiceImpTest {

    @Autowired
    private QRService qrService;

    @Autowired
    RestaurantTableService restaurantTableService;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
    }

    @Test
    void shouldGenerate() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(1);
        File file = qrService.generate(restaurantTable);
        assertEquals("QR code - Table n", file.getName().substring(0, 17));
    }
}