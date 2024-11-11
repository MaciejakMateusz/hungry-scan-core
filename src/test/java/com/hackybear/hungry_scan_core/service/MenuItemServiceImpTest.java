package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuItemMapper;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
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
    private MenuItemMapper menuItemMapper;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @Transactional
    public void shouldFindById() throws LocalizedException {
        MenuItemFormDTO menuItem = menuItemService.findById(12L);
        assertEquals("Sałatka z grillowanym kurczakiem i awokado", menuItem.name().defaultTranslation());
    }

    @Test
    public void shouldNotFindById() {
        LocalizedException localizedException = assertThrows(LocalizedException.class, () -> menuItemService.findById(666L));
        assertEquals("Danie z podanym ID = 666 nie istnieje.", localizedException.getLocalizedMessage());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldInsertNew() throws Exception {
        MenuItem newMenuItem = createMenuItem(
                "Burger",
                "Z mięsem wegańskim",
                "/public/assets/burger.png");
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(newMenuItem);
        Long activeMenuId = userService.getActiveMenuId();
        menuItemService.save(menuItemFormDTO, activeMenuId);
        MenuItemFormDTO menuItem = menuItemService.findById(34L);
        assertEquals("Z mięsem wegańskim", menuItem.description().defaultTranslation());
        assertEquals("/public/assets/burger.png", menuItem.imageName());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldNotInsertWithBlankName() throws LocalizedException {
        MenuItem menuItemBlank = createMenuItem(
                "",
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");

        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItemBlank);
        Long activeMenuId = userService.getActiveMenuId();
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItemFormDTO, activeMenuId));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldNotInsertWithNullName() throws LocalizedException {
        MenuItem menuItemNull = createMenuItem(
                null,
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItemNull);
        Long activeMenuId = userService.getActiveMenuId();
        assertThrows(DataIntegrityViolationException.class, () -> menuItemService.save(menuItemFormDTO, activeMenuId));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldNotInsertWithZeroPrice() throws LocalizedException {
        MenuItem menuItem = createMenuItem(
                "Cheeseburger",
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");
        menuItem.setPrice(Money.of(0.00));

        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItem);
        Long activeMenuId = userService.getActiveMenuId();
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItemFormDTO, activeMenuId));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldNotInsertWithNullPrice() throws LocalizedException {
        MenuItem menuItem = createMenuItem(
                "Cheeseburger",
                "Z mięsem i serem wegańskim.",
                "/public/assets/cheeseburger.png");
        menuItem.setPrice(null);

        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItem);
        Long activeMenuId = userService.getActiveMenuId();
        assertThrows(ConstraintViolationException.class, () -> menuItemService.save(menuItemFormDTO, activeMenuId));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldUpdate() throws Exception {
        MenuItem existingMenuItem = menuItemRepository.findById(23L).orElseThrow();
        
        assertEquals("Pizza Capricciosa", existingMenuItem.getName().getDefaultTranslation());

        existingMenuItem.setName(getDefaultTranslation("Burger wege"));
        existingMenuItem.setPrice(Money.of(44.12));
        existingMenuItem.setImageName("/public/assets/wege-burger.png");
        MenuItemFormDTO existingMenuItemDTO = menuItemMapper.toFormDTO(existingMenuItem);
        Long activeMenuId = userService.getActiveMenuId();
        menuItemService.update(existingMenuItemDTO, activeMenuId);

        MenuItemFormDTO updatedMenuItem = menuItemService.findById(23L);
        assertEquals("Burger wege", updatedMenuItem.name().defaultTranslation());
        assertEquals(Money.of(44.12), updatedMenuItem.price());
        assertEquals("/public/assets/wege-burger.png", updatedMenuItem.imageName());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "admin@example.com")
    public void shouldDelete() throws LocalizedException {
        MenuItemFormDTO menuItem = menuItemService.findById(23L);
        assertEquals("Pizza Capricciosa", menuItem.name().defaultTranslation());
        Long activeMenuId = userService.getActiveMenuId();
        menuItemService.delete(23L, activeMenuId);
        assertThrows(LocalizedException.class, () -> menuItemService.findById(23L));
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
        menuItem.setCategoryId(2L);
        return menuItem;
    }

    private Translatable getDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        return translatable;
    }

}