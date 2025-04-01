package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuItemMapper;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.enums.Banner;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import com.hackybear.hungry_scan_core.test_utils.MenuItemFactory;
import com.hackybear.hungry_scan_core.utility.Money;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenuItemControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private MenuItemFactory menuItemFactory;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MenuItemMapper menuItemMapper;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    void shouldShowMenuItemById() throws Exception {
        MenuItemFormDTO menuItemFormDTO = fetchMenuItemFormDTO(4L);
        assertEquals("Roladki z bakłażana", menuItemFormDTO.name().defaultTranslation());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowMenuItem() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/items/show", 4, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotShowMenuItemById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/items/show", 55, status().isBadRequest());
        assertEquals("Danie z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldAddNewMenuItem() throws Exception {
        MenuItem menuItem = createMenuItem();
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItem);
        Optional<Integer> maxDisplayOrder = menuItemRepository.findMaxDisplayOrder(2L);
        assertEquals(5, maxDisplayOrder.orElse(0));

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItemFormDTO);

        MenuItemFormDTO persistedMenuItem = fetchMenuItemFormDTO(34L);

        assertEquals("Sample Item", persistedMenuItem.name().defaultTranslation());
        assertEquals("Sample description", persistedMenuItem.description().defaultTranslation());
        assertEquals(5, persistedMenuItem.additionalIngredients().size());
        assertEquals(1, persistedMenuItem.allergens().size());
        assertEquals(2, persistedMenuItem.labels().size());
        assertEquals("/public/assets/sample.png", persistedMenuItem.imageName());
        Integer displayOrder = menuItemRepository.findById(persistedMenuItem.id()).orElseThrow().getDisplayOrder();
        assertEquals(6, displayOrder);
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToItemsAdd() throws Exception {
        MenuItem menuItem = createMenuItem();
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItem);
        apiRequestUtils.postAndExpect("/api/cms/items/add", menuItemFormDTO, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldNotAddWithIncorrectName() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setName(getDefaultTranslation(""));
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItem);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/items/add", menuItemFormDTO);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdate() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(23L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        persistedMenuItem.setName(getDefaultTranslation("Updated Item"));
        persistedMenuItem.setDescription(getDefaultTranslation("Updated Description"));
        persistedMenuItem.setImageName("/public/assets/updated.png");
        persistedMenuItem.setBanners(Set.of(Banner.NEW, Banner.PROMO));
        persistedMenuItem.setPromoPrice(Money.of(25));

        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 23, MenuItemFormDTO.class);
        assertEquals("Updated Item", updatedMenuItem.name().defaultTranslation());
        assertEquals("Updated Description", updatedMenuItem.description().defaultTranslation());
        assertEquals("/public/assets/updated.png", updatedMenuItem.imageName());
        assertTrue(updatedMenuItem.banners().contains(Banner.NEW));
        assertTrue(updatedMenuItem.banners().contains(Banner.PROMO));
        assertEquals(Money.of(25), Money.of(updatedMenuItem.promoPrice()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotUpdateWithIncorrectPromoPrice() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(23L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        persistedMenuItem.setBanners(Set.of(Banner.BESTSELLER, Banner.PROMO));
        persistedMenuItem.setPromoPrice(BigDecimal.ZERO);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        Map<?, ?> errors =
                apiRequestUtils.patchAndExpectErrors("/api/cms/items/update", menuItemFormDTO);
        assertFalse(errors.isEmpty());
        assertEquals("Cena promocyjna powinna był uzupełniona.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldSwitchCategory() throws Exception {
        //GIVEN
        List<CategoryDTO> categories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);

        CategoryDTO oldCategory = categories.get(4);
        assertEquals("Pizza", oldCategory.name().defaultTranslation());
        assertEquals(5, oldCategory.menuItems().size());

        CategoryDTO newCategory = categories.getFirst();
        assertEquals("Przystawki", newCategory.name().defaultTranslation());
        assertEquals(5, newCategory.menuItems().size());

        MenuItemFormDTO existingMenuItemDTO = fetchMenuItemFormDTO(23L);
        assertEquals(existingMenuItemDTO.categoryId(), oldCategory.id());
        MenuItem existingMenuItem = menuItemMapper.toMenuItem(existingMenuItemDTO);
        existingMenuItem.setCategoryId(newCategory.id());
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(existingMenuItem);

        //WHEN
        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        //THEN
        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 23, MenuItemFormDTO.class);

        List<CategoryDTO> updatedCategories =
                apiRequestUtils.fetchAsList(
                        "/api/cms/categories", CategoryDTO.class);
        List<MenuItemSimpleDTO> updatedNewItems = updatedCategories.getFirst().menuItems();
        assertEquals(6, updatedNewItems.size());
        assertEquals(1, updatedNewItems.getFirst().displayOrder());
        assertEquals(2, updatedNewItems.get(1).displayOrder());
        assertEquals(3, updatedNewItems.get(2).displayOrder());
        assertEquals(4, updatedNewItems.get(3).displayOrder());
        assertEquals(5, updatedNewItems.get(4).displayOrder());
        assertEquals(6, updatedNewItems.get(5).displayOrder());
        assertEquals(1L, updatedMenuItem.categoryId());
        boolean isUpdatedMenuItemPresent = updatedNewItems
                .stream()
                .anyMatch(menuItem -> Objects.equals(menuItem.id(), updatedMenuItem.id()));
        assertTrue(isUpdatedMenuItemPresent);

        List<MenuItemSimpleDTO> updatedOldItems = updatedCategories.get(4).menuItems();
        assertEquals(4, updatedOldItems.size());
        assertEquals(1, updatedNewItems.getFirst().displayOrder());
        assertEquals(2, updatedNewItems.get(1).displayOrder());
        assertEquals(3, updatedNewItems.get(2).displayOrder());
        assertEquals(4, updatedNewItems.get(3).displayOrder());
        isUpdatedMenuItemPresent = updatedOldItems
                .stream()
                .anyMatch(menuItem -> Objects.equals(menuItem.id(), updatedMenuItem.id()));
        assertFalse(isUpdatedMenuItemPresent);
    }

    @Test
    @WithMockUser(roles = {"MANAGER"}, username = "netka@test.com")
    @Transactional
    @Rollback
    void shouldUpdateDisplayOrders() throws Exception {
        Category category = categoryRepository.findById(1L).orElseThrow();
        List<MenuItem> menuItems = category.getMenuItems().stream().toList();

        assertEquals(1, menuItems.getFirst().getDisplayOrder());
        assertEquals("Krewetki marynowane w cytrynie", menuItems.getFirst().getName().getDefaultTranslation());

        assertEquals(2, menuItems.get(1).getDisplayOrder());

        assertEquals(3, menuItems.get(2).getDisplayOrder());
        assertEquals("Krewetki w tempurze", menuItems.get(2).getName().getDefaultTranslation());

        assertEquals(4, menuItems.get(3).getDisplayOrder());
        assertEquals(5, menuItems.get(4).getDisplayOrder());
        assertEquals("Nachos z sosem serowym", menuItems.get(4).getName().getDefaultTranslation());

        menuItems.getFirst().setDisplayOrder(5);
        menuItems.get(4).setDisplayOrder(1);

        menuItems.get(3).setDisplayOrder(3);
        menuItems.get(2).setDisplayOrder(4);

        List<MenuItemSimpleDTO> menuItemDTOs = menuItems.stream().map(menuItemMapper::toDTO).toList();
        apiRequestUtils.patchAndExpect200("/api/cms/items/display-orders", menuItemDTOs);
        category = categoryRepository.findById(1L).orElseThrow();
        List<MenuItem> updatedMenuItems = category.getMenuItems().stream().sorted().toList();
        assertEquals("Nachos z sosem serowym", updatedMenuItems.getFirst().getName().getDefaultTranslation());
        assertEquals(1, updatedMenuItems.getFirst().getDisplayOrder());

        assertEquals("Krewetki marynowane w cytrynie", updatedMenuItems.get(4).getName().getDefaultTranslation());
        assertEquals(5, updatedMenuItems.get(4).getDisplayOrder());

        assertEquals("Krewetki w tempurze", updatedMenuItems.get(3).getName().getDefaultTranslation());
        assertEquals(4, updatedMenuItems.get(3).getDisplayOrder());

        assertEquals("Roladki z bakłażana", updatedMenuItems.get(2).getName().getDefaultTranslation());
        assertEquals(3, updatedMenuItems.get(2).getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldDeleteMenuItem() throws Exception {
        MenuItemFormDTO menuItem = fetchMenuItemFormDTO(25L);
        assertEquals("Pizza Quattro Formaggi", menuItem.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/items/delete", 25L);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/items/show", 25, status().isBadRequest());
        assertEquals("Danie z podanym ID = 25 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdateDisplayOrdersAfterRemoval() throws Exception {
        MenuItemFormDTO menuItem = fetchMenuItemFormDTO(2L);
        assertEquals("Carpaccio z polędwicy wołowej", menuItem.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/items/delete", 2L);
        Category category = categoryRepository.findById(1L).orElseThrow();
        List<MenuItem> menuItems = category.getMenuItems().stream().sorted().toList();

        assertEquals(4, menuItems.size());
        MenuItem secondMenuItem = menuItems.getFirst();
        assertNotEquals("Carpaccio z polędwicy wołowej", secondMenuItem.getName().getDefaultTranslation());
        MenuItem thirdMenuItem = menuItems.get(2);
        assertEquals("Roladki z bakłażana", thirdMenuItem.getName().getDefaultTranslation());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemoveFirst() throws Exception {
        MenuItemFormDTO menuItem = fetchMenuItemFormDTO(1L);
        assertEquals("Krewetki marynowane w cytrynie", menuItem.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/items/delete", 1L);
        Category category = categoryRepository.findById(1L).orElseThrow();
        List<MenuItem> menuItems = category.getMenuItems().stream().sorted().toList();
        assertEquals(4, menuItems.size());

        assertEquals(
                "Carpaccio z polędwicy wołowej",
                menuItems.getFirst().getName().getDefaultTranslation());
        assertEquals(1, menuItems.getFirst().getDisplayOrder());

        assertEquals(2, menuItems.get(1).getDisplayOrder());

        assertEquals(
                "Roladki z bakłażana",
                menuItems.get(2).getName().getDefaultTranslation());
        assertEquals(3, menuItems.get(2).getDisplayOrder());

        assertEquals(4, menuItems.get(3).getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Transactional
    @Rollback
    void shouldNotAllowUnauthorizedAccessToRemoveMenuItem() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/items/delete", 15L, status().isForbidden());
    }

    private MenuItemFormDTO fetchMenuItemFormDTO(Long id) throws Exception {
        return apiRequestUtils.postObjectExpect200(
                "/api/cms/items/show", id, MenuItemFormDTO.class);
    }

    private MenuItem createMenuItem() {
        return menuItemFactory.createMenuItem(
                "Sample Item",
                "Sample description",
                2L,
                Money.of(23.55));
    }

    private Translatable getDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        return translatable;
    }

}