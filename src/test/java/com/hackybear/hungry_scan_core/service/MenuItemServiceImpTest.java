package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.utility.Money;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    public void shouldFindAll() {
        List<MenuItem> menuItems = menuItemService.findAll();
        assertEquals(33, menuItems.size());
        assertEquals("Makaron z pesto bazyliowym", menuItems.get(29).getName().getDefaultTranslation());
    }

    @Test
    public void shouldFindById() throws LocalizedException {
        MenuItem menuItem = menuItemService.findById(12);
        assertEquals("Sałatka z grillowanym kurczakiem i awokado", menuItem.getName().getDefaultTranslation());
    }

    @Test
    public void shouldNotFindById() {
        LocalizedException localizedException = assertThrows(LocalizedException.class, () -> menuItemService.findById(666));
        assertEquals("Danie z podanym ID = 666 nie istnieje.", localizedException.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldInsertNew() throws Exception {
        MenuItem newMenuItem = createMenuItem(
                "Burger",
                "Z mięsem wegańskim",
                "/public/assets/burger.png");
        menuItemService.save(newMenuItem);
        MenuItem menuItem = menuItemService.findById(newMenuItem.getId());
        assertEquals("Z mięsem wegańskim", menuItem.getDescription().getDefaultTranslation());
        assertEquals("/public/assets/burger.png", menuItem.getImageName());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotInsertWithIncorrectName() {
        MenuItem menuItem = createMenuItem(
                "Cheeseburger",
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");

        menuItem.setName(getDefaultTranslation(""));
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setName(null);
        assertThrows(NullPointerException.class, () -> menuItemService.save(menuItem));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldNotInsertWithIncorrectPrice() {
        MenuItem menuItem = createMenuItem(
                "Cheeseburger",
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");

        menuItem.setPrice(Money.of(0.00));
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItem));

        menuItem.setPrice(null);
        assertThrows(NullPointerException.class, () -> menuItemService.save(menuItem));
    }

    @Test
    @Transactional
    @Rollback
    public void shouldUpdate() throws Exception {
        MenuItem existingMenuItem = menuItemService.findById(23);
        assertEquals("Pizza Capricciosa", existingMenuItem.getName().getDefaultTranslation());

        existingMenuItem.setName(getDefaultTranslation("Burger wege"));
        existingMenuItem.setPrice(Money.of(44.12));
        existingMenuItem.setImageName("/public/assets/wege-burger.png");
        menuItemService.save(existingMenuItem);

        MenuItem updatedMenuItem = menuItemService.findById(23);
        assertEquals("Burger wege", updatedMenuItem.getName().getDefaultTranslation());
        assertEquals(Money.of(44.12), updatedMenuItem.getPrice());
        assertEquals("/public/assets/wege-burger.png", updatedMenuItem.getImageName());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldDelete() throws LocalizedException {
        MenuItem menuItem = menuItemService.findById(23);
        assertEquals("Pizza Capricciosa", menuItem.getName().getDefaultTranslation());
        menuItemService.delete(23);
        assertThrows(LocalizedException.class, () -> menuItemService.findById(23));
    }

    private MenuItem createMenuItem(String name,
                                    String description,
                                    String imageName) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(getDefaultTranslation(name));
        menuItem.setDescription(getDefaultTranslation(description));
        menuItem.setPrice(Money.of(42.50));
        menuItem.setImageName(imageName);
        menuItem.setDisplayOrder(6);
        menuItem.setCategoryId(2);
        return menuItem;
    }

    private Translatable getDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        return translatable;
    }
}