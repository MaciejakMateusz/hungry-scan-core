package pl.rarytas.rarytas_restaurantside.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.io.IOException;
import java.math.BigDecimal;
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
public class MenuItemServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MenuItemService menuItemService;

    @Test
    @Order(1)
    public void shouldReturnAll() {
        assertEquals(40, getMenuItems().size());
    }

    @Test
    @Order(2)
    public void shouldInsertNew() throws IOException {
        MenuItem newMenuItem = createMenuItem("Burger", 2, "Z mięsem wegańskim", "Bułka, mięso sojowe, sałata, ogórek konserwowy, chrzan żurawinowy", BigDecimal.valueOf(20.00));
        menuItemService.save(newMenuItem, null);
        MenuItem menuItem = menuItemService.findById(newMenuItem.getId()).orElse(new MenuItem());
        assertEquals(newMenuItem.getId(), menuItem.getId());
    }

    @Test
    public void shouldNotInsertNew() {
        MenuItem menuItem = createMenuItem("Cheeseburger", 3, "Z mięsem i serem wegańskim.", "Bułka, mięso sojowe, sałata, ogórek konserwowy, chrzan żurawinowy.", BigDecimal.valueOf(21.00));

        menuItem.setName("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setName(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setName("Test");
        menuItem.setPrice(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setPrice(BigDecimal.valueOf(0.5));
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setDescription("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));

        menuItem.setDescription(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem, null));
    }

    @Test
    @Order(3)
    public void shouldUpdate() throws IOException {
        MenuItem existingMenuItem = menuItemService.findById(41).orElse(new MenuItem());
        existingMenuItem.setName("Burger wege");
        menuItemService.save(existingMenuItem, null);
        MenuItem updatedMenuItem = menuItemService.findById(41).orElse(new MenuItem());
        assertEquals("Burger wege", updatedMenuItem.getName());
    }

    @Test
    @Order(4)
    public void shouldDelete() {
        MenuItem menuItem = menuItemService.findById(41).orElseThrow();
        assertEquals("Burger wege", menuItem.getName());
        menuItemService.delete(menuItem);
        assertThrows(NoSuchElementException.class, () -> menuItemService.findById(41).orElseThrow());
    }

    private List<MenuItem> getMenuItems() {
        return menuItemService.findAll();
    }

    private MenuItem createMenuItem(String name,
                                    int categoryId,
                                    String description,
                                    String ingredients,
                                    BigDecimal price) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setCategory(categoryService.findById(categoryId).orElseThrow());
        menuItem.setDescription(description);
        menuItem.setIngredients(ingredients);
        menuItem.setPrice(price);
        return menuItem;
    }
}