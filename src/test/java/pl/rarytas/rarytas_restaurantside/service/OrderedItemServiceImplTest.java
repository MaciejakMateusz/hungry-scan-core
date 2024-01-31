package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void shouldFindAll() {
        List<OrderedItem> orderedItems = orderedItemService.findAll();
        assertEquals(5, orderedItems.size());
    }

    @Test
    void shouldFindById() {
        OrderedItem orderedItem = orderedItemService.findById(1).orElse(new OrderedItem());
        assertEquals(3, orderedItem.getMenuItem().getId());
        assertEquals(2, orderedItem.getQuantity());
    }

    @Test
    void shouldNotFindById() {
        assertThrows(NoSuchElementException.class, () -> orderedItemService.findById(8).orElseThrow());
    }

    @Test
    void shouldDelete() {
        OrderedItem orderedItem = orderedItemService.findById(6).orElse(new OrderedItem());
        orderedItemService.delete(orderedItem);
        assertThrows(NoSuchElementException.class, () -> orderedItemService.findById(6).orElseThrow());
    }

    @Test
    void shouldNotDelete() {
        OrderedItem orderedItem = orderedItemService.findById(1).orElse(new OrderedItem());
        assertThrows(DataIntegrityViolationException.class, () -> orderedItemService.delete(orderedItem));
    }
}