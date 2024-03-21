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
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MenuItemServiceImpTest {

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
    public void shouldInsertNew() throws LocalizedException {
        MenuItem newMenuItem = createMenuItem(
                "Burger",
                2,
                "Z mięsem wegańskim",
                "Bułka, mięso sojowe, " + "sałata, ogórek konserwowy, chrzan żurawinowy",
                BigDecimal.valueOf(20.00),
                "/public/assets/burger.png");
        menuItemService.save(newMenuItem);
        MenuItem menuItem = menuItemService.findById(newMenuItem.getId());
        assertEquals(newMenuItem.getId(), menuItem.getId());
    }

    @Test
    public void shouldNotInsertNew() throws LocalizedException {
        MenuItem menuItem = createMenuItem(
                "Cheeseburger",
                3,
                "Z mięsem i serem wegańskim.",
                "Bułka, mięso sojowe, sałata, " + "ogórek konserwowy, chrzan żurawinowy.",
                BigDecimal.valueOf(21.00),
                "/public/assets/cheeseburger.png");

        menuItem.setName("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setName(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setName("Test");
        menuItem.setPrice(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setPrice(BigDecimal.valueOf(0.5));
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setDescription("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setDescription(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));
    }

    @Test
    @Order(3)
    public void shouldUpdate() throws LocalizedException {
        MenuItem existingMenuItem = menuItemService.findById(41);
        existingMenuItem.setName("Burger wege");
        existingMenuItem.setImageName("/public/assets/wege-burger.png");
        menuItemService.save(existingMenuItem);
        MenuItem updatedMenuItem = menuItemService.findById(41);
        assertEquals("Burger wege", updatedMenuItem.getName());
        assertEquals("/public/assets/wege-burger.png", updatedMenuItem.getImageName());
    }

    @Test
    @Order(4)
    public void shouldDelete() throws LocalizedException {
        MenuItem menuItem = menuItemService.findById(41);
        assertEquals("Burger wege", menuItem.getName());
        menuItemService.delete(41);
        assertThrows(LocalizedException.class, () -> menuItemService.findById(41));
    }

    @Test
    @Order(5)
    public void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> menuItemService.findById(321));
    }

    private List<MenuItem> getMenuItems() {
        return menuItemService.findAll();
    }

    private MenuItem createMenuItem(String name,
                                    int categoryId,
                                    String description,
                                    String ingredients,
                                    BigDecimal price,
                                    String imageName) throws LocalizedException {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setCategory(categoryService.findById(categoryId));
        menuItem.setDescription(description);
        menuItem.setIngredients(ingredients);
        menuItem.setPrice(price);
        menuItem.setImageName(imageName);
        return menuItem;
    }
}