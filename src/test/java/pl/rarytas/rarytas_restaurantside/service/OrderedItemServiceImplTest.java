package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderedItemServiceImplTest {

    @Autowired
    private OrderedItemService orderedItemService;

    @Test
    @Order(1)
    void shouldFindAll() {
        List<OrderedItem> orderedItems = orderedItemService.findAll();
        assertEquals(4, orderedItems.size());
    }

    @Test
    @Order(2)
    void shouldFindById() throws LocalizedException {
        OrderedItem orderedItem = orderedItemService.findById(1L);
        assertEquals(3, orderedItem.getMenuItem().getId());
        assertEquals(2, orderedItem.getQuantity());
    }

    @Test
    @Order(3)
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> orderedItemService.findById(321L));
    }

    @Test
    @Order(4)
    void shouldToggleIsReadyToServe() throws LocalizedException {
        OrderedItem orderedItem = orderedItemService.findById(1L);
        assertFalse(orderedItem.isReadyToServe());

        orderedItemService.toggleIsReadyToServe(1L);
        orderedItem = orderedItemService.findById(1L);

        assertTrue(orderedItem.isReadyToServe());

        orderedItemService.toggleIsReadyToServe(1L);
        orderedItem = orderedItemService.findById(1L);
        assertFalse(orderedItem.isReadyToServe());

    }

}