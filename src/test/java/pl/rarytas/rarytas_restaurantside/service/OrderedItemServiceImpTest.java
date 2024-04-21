package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderedItemServiceImpTest {

    @Autowired
    private OrderedItemService orderedItemService;

    @Test
    void shouldFindAll() {
        List<OrderedItem> orderedItems = orderedItemService.findAll();
        assertEquals(5, orderedItems.size());
    }

    @Test
    void shouldFindById() throws LocalizedException {
        OrderedItem orderedItem = orderedItemService.findById(1L);
        assertEquals(3, orderedItem.getMenuItemVariant().getId());
        assertEquals(2, orderedItem.getQuantity());
    }

    @Test
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> orderedItemService.findById(321L));
    }

}