package pl.rarytas.hungry_scan_core.service;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.hungry_scan_core.entity.Category;
import pl.rarytas.hungry_scan_core.entity.MenuItem;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.service.interfaces.CategoryService;
import pl.rarytas.hungry_scan_core.service.interfaces.MenuItemService;
import pl.rarytas.hungry_scan_core.utility.Money;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MenuItemServiceImpTest {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private CategoryService categoryService;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    public void shouldFindAll() {
        List<MenuItem> menuItems = menuItemService.findAll();
        assertEquals(30, menuItems.size());
        assertEquals("Makaron z pesto bazyliowym", menuItems.get(29).getName());
    }

    @Test
    public void shouldFindAllByCategoryId() {
        List<MenuItem> menuItems = menuItemService.findAllByCategoryId(3);
        assertEquals(5, menuItems.size());
        assertEquals("Sałatka z rukolą, serem kozim i suszonymi żurawinami", menuItems.get(2).getName());
    }

    @Test
    public void shouldNotFindAllByCategoryId() {
        List<MenuItem> menuItems = menuItemService.findAllByCategoryId(666);
        assertTrue(menuItems.isEmpty());
    }

    @Test
    public void shouldFindById() throws LocalizedException {
        MenuItem menuItem = menuItemService.findById(12);
        assertEquals("Sałatka z grillowanym kurczakiem i awokado", menuItem.getName());
    }

    @Test
    public void shouldNotFindById() {
        LocalizedException localizedException = assertThrows(LocalizedException.class, () -> menuItemService.findById(666));
        assertEquals("Danie z podanym ID = 666 nie istnieje.", localizedException.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldInsertNew() throws LocalizedException {
        MenuItem newMenuItem = createMenuItem(
                "Burger",
                categoryService.findById(2),
                "Z mięsem wegańskim",
                "/public/assets/burger.png");
        menuItemService.save(newMenuItem);
        MenuItem menuItem = menuItemService.findById(newMenuItem.getId());
        assertEquals("Z mięsem wegańskim", menuItem.getDescription());
        assertEquals("/public/assets/burger.png", menuItem.getImageName());
    }

    @Test
    public void shouldNotInsertWithIncorrectName() throws LocalizedException {
        MenuItem menuItem = createMenuItem(
                "Cheeseburger",
                categoryService.findById(3),
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");

        menuItem.setName("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setName(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setDescription("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setDescription(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setDescription(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));
    }

    @Test
    public void shouldNotInsertWithIncorrectDescription() throws LocalizedException {
        MenuItem menuItem = createMenuItem(
                "Cheeseburger",
                categoryService.findById(3),
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");

        menuItem.setDescription("");
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setDescription(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));
    }

    @Test
    public void shouldNotInsertWithIncorrectPrice() throws LocalizedException {
        MenuItem menuItem = createMenuItem(
                "Cheeseburger",
                categoryService.findById(3),
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");

        menuItem.setPrice(Money.of(0.00));
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setPrice(null);
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdate() throws LocalizedException {
        MenuItem existingMenuItem = menuItemService.findById(23);
        assertEquals("Pizza Capricciosa", existingMenuItem.getName());

        existingMenuItem.setName("Burger wege");
        existingMenuItem.setPrice(Money.of(44.12));
        existingMenuItem.setImageName("/public/assets/wege-burger.png");
        menuItemService.save(existingMenuItem);

        MenuItem updatedMenuItem = menuItemService.findById(23);
        assertEquals("Burger wege", updatedMenuItem.getName());
        assertEquals(Money.of(44.12), updatedMenuItem.getPrice());
        assertEquals("/public/assets/wege-burger.png", updatedMenuItem.getImageName());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldDelete() throws LocalizedException {
        MenuItem menuItem = menuItemService.findById(23);
        assertEquals("Pizza Capricciosa", menuItem.getName());
        menuItemService.delete(23);
        assertThrows(LocalizedException.class, () -> menuItemService.findById(23));
    }

    private MenuItem createMenuItem(String name,
                                    Category category,
                                    String description,
                                    String imageName) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setCategory(category);
        menuItem.setDescription(description);
        menuItem.setPrice(Money.of(42.50));
        menuItem.setImageName(imageName);
        return menuItem;
    }
}