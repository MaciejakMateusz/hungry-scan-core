package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RestaurantTableServiceImpTest {

    @Autowired
    private RestaurantTableService restaurantTableService;


    @Test
    void shouldFindAll() {
        List<RestaurantTable> restaurantTables = restaurantTableService.findAll();
        assertEquals(19, restaurantTables.size());
        assertEquals("5afb9629-990a-4934-87f2-793b1aa2f35e", restaurantTables.get(3).getToken());
    }

    @Test
    void shouldFindById() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(10);
        assertEquals("88ca9c82-e630-40f2-9bf9-47f7d14f6bff", restaurantTable.getToken());
    }

    @Test
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> restaurantTableService.findById(98));
    }

    @Test
    void shouldFindByToken() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findByToken("65b6bb94-da99-4ced-8a94-5860fe95e708");
        assertEquals(15, restaurantTable.getId());
    }

    @Test
    void shouldNotFindByToken() {
        assertThrows(LocalizedException.class, () ->
                restaurantTableService.findByToken("65b6bb94-da99-yyymleko-8a94-5860fe95e708"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldSave() throws LocalizedException {
        RestaurantTable restaurantTable = createRestaurantTable();
        restaurantTable.setId(20);
        restaurantTableService.save(restaurantTable);
        RestaurantTable persistedRestaurantTable = restaurantTableService.findById(20);
        assertNotNull(restaurantTable);
        assertEquals(restaurantTable.getToken(), persistedRestaurantTable.getToken());
    }

    @Test
    void shouldNotSave() {
        RestaurantTable restaurantTable = createRestaurantTable();
        restaurantTable.setId(21);
        restaurantTable.setToken(null);
        assertThrows(TransactionSystemException.class, () -> restaurantTableService.save(restaurantTable));
    }

    @Test
    @Transactional
    void shouldToggleActivation() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9);
        assertFalse(restaurantTable.isActive());

        restaurantTableService.toggleActivation(9);
        restaurantTable = restaurantTableService.findById(9);
        assertTrue(restaurantTable.isActive());

        restaurantTableService.toggleActivation(9);
        restaurantTable = restaurantTableService.findById(9);
        assertFalse(restaurantTable.isActive());
    }

    @Test
    void shouldNotToggle() {
        assertThrows(LocalizedException.class, () -> restaurantTableService.toggleActivation(55));
    }

    private RestaurantTable createRestaurantTable() {
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setToken(UUID.randomUUID().toString());
        return restaurantTable;
    }
}