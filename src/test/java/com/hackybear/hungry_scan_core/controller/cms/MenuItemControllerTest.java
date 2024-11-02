package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuItemMapper;
import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.repository.CategoryRepository;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
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
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewMenuItem() throws Exception {
        MenuItem menuItem = createMenuItem();
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItem);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItemFormDTO);

        MenuItemFormDTO persistedMenuItem = fetchMenuItemFormDTO(36L);

        assertEquals("Sample Item", persistedMenuItem.name().defaultTranslation());
        assertEquals("Sample description", persistedMenuItem.description().defaultTranslation());
        assertEquals(5, persistedMenuItem.additionalIngredients().size());
        assertEquals(1, persistedMenuItem.allergens().size());
        assertEquals(2, persistedMenuItem.labels().size());
        assertEquals("/public/assets/sample.png", persistedMenuItem.imageName());
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
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateExistingMenuItem() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(23L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        persistedMenuItem.setName(getDefaultTranslation("Updated Item"));
        persistedMenuItem.setDescription(getDefaultTranslation("Updated Description"));
        persistedMenuItem.setImageName("/public/assets/updated.png");
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 23, MenuItemFormDTO.class);
        assertEquals("Updated Item", updatedMenuItem.name().defaultTranslation());
        assertEquals("Updated Description", updatedMenuItem.description().defaultTranslation());
        assertEquals("/public/assets/updated.png", updatedMenuItem.imageName());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldSwitchCategory() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(23L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        persistedMenuItem.setCategoryId(1L);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 23, MenuItemFormDTO.class);

        Category category = getCategoryById(1L);
        assertEquals(1L, updatedMenuItem.categoryId());
        boolean isUpdatedMenuItemPresent = category
                .getMenuItems()
                .stream()
                .anyMatch(menuItem -> Objects.equals(menuItem.getId(), updatedMenuItem.id()));
        assertTrue(isUpdatedMenuItemPresent);
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToBigger() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(1L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        assertEquals(1, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(3);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItemFormDTO.class);
        assertEquals(3, updatedMenuItem.displayOrder());

        MenuItemFormDTO thirdToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 3, MenuItemFormDTO.class);
        assertEquals(2, thirdToSecond.displayOrder());

        MenuItemFormDTO secondToFirst =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 2, MenuItemFormDTO.class);
        assertEquals(1, secondToFirst.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToLower() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(5L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        assertEquals(5, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(2);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItemFormDTO.class);
        assertEquals(2, updatedMenuItem.displayOrder());

        MenuItemFormDTO secondToThird =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 2, MenuItemFormDTO.class);
        assertEquals(3, secondToThird.displayOrder());

        MenuItemFormDTO fourthToFifth =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItemFormDTO.class);
        assertEquals(5, fourthToFifth.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToLastWithTooBigValue() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(1L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        assertEquals(1, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(15);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItemFormDTO.class);
        assertEquals(5, updatedMenuItem.displayOrder());

        MenuItemFormDTO fifthToFourth =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItemFormDTO.class);
        assertEquals(4, fifthToFourth.displayOrder());

        MenuItemFormDTO thirdToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 3, MenuItemFormDTO.class);
        assertEquals(2, thirdToSecond.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToFirstWithTooLowValue() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(3L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        assertEquals(3, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(-2);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 3, MenuItemFormDTO.class);
        assertEquals(1, updatedMenuItem.displayOrder());

        MenuItemFormDTO firstToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItemFormDTO.class);
        assertEquals(2, firstToSecond.displayOrder());

        MenuItemFormDTO secondToThird =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 2, MenuItemFormDTO.class);
        assertEquals(3, secondToThird.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderFromLastToFirst() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(5L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        assertEquals(5, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(1);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItemFormDTO.class);
        assertEquals(1, updatedMenuItem.displayOrder());

        MenuItemFormDTO firstToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItemFormDTO.class);
        assertEquals(2, firstToSecond.displayOrder());

        MenuItemFormDTO fourthToFifth =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItemFormDTO.class);
        assertEquals(5, fourthToFifth.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderFromFirstToLast() throws Exception {
        MenuItemFormDTO persistedDTO = fetchMenuItemFormDTO(1L);
        MenuItem persistedMenuItem = menuItemMapper.toMenuItem(persistedDTO);
        assertEquals(1, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(5);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(persistedMenuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", menuItemFormDTO);

        MenuItemFormDTO updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItemFormDTO.class);
        assertEquals(5, updatedMenuItem.displayOrder());

        MenuItemFormDTO secondToFirst =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 2, MenuItemFormDTO.class);
        assertEquals(1, secondToFirst.displayOrder());

        MenuItemFormDTO fifthToFourth =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItemFormDTO.class);
        assertEquals(4, fifthToFourth.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewWithExistingDisplayOrder() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setDisplayOrder(2);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItem);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItemFormDTO);

        Category category = getCategoryById(2L);
        List<MenuItem> menuItems = category.getMenuItems();

        assertEquals(6, menuItems.size());
        assertEquals("Sample Item", menuItems.get(1).getName().getDefaultTranslation());
        assertEquals(2, menuItems.get(1).getDisplayOrder());
        assertEquals("Spaghetti Bolognese", menuItems.get(0).getName().getDefaultTranslation());
        assertEquals("Penne Carbonara", menuItems.get(2).getName().getDefaultTranslation());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddFirstToEmptyCategoryAssertDisplayOrder1() throws Exception {
        MenuItem menuItem = menuItemFactory.createMenuItem(
                "Sample Item",
                "Sample description",
                9L,
                Money.of(23.55));
        menuItem.setDisplayOrder(22);
        MenuItemFormDTO menuItemFormDTO = menuItemMapper.toFormDTO(menuItem);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItemFormDTO);

        MenuItemFormDTO persistedMenuItem = apiRequestUtils.postObjectExpect200(
                "/api/cms/items/show", 37, MenuItemFormDTO.class);
        assertEquals("Sample Item", persistedMenuItem.name().defaultTranslation());
        assertEquals(1, persistedMenuItem.displayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldHandleBoundaryDisplayOrders() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setDisplayOrder(Integer.MIN_VALUE);
        MenuItemFormDTO minIntOrder = menuItemMapper.toFormDTO(menuItem);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", minIntOrder);

        Category category = getCategoryById(2L);
        List<MenuItem> menuItems = category.getMenuItems();
        assertEquals(6, menuItems.size());
        menuItem = menuItems.get(0);
        assertEquals("Sample Item", menuItem.getName().getDefaultTranslation());
        assertEquals(1, menuItem.getDisplayOrder());
        assertEquals("Spaghetti Bolognese", menuItems.get(1).getName().getDefaultTranslation());
        assertEquals(2, menuItems.get(1).getDisplayOrder());

        menuItem.setDisplayOrder(Integer.MAX_VALUE);
        MenuItemFormDTO maxIntOrder = menuItemMapper.toFormDTO(menuItem);
        apiRequestUtils.patchAndExpect200("/api/cms/items/update", maxIntOrder);

        category = getCategoryById(2L);
        menuItems = category.getMenuItems();
        menuItem = menuItems.get(menuItems.size() - 1);

        assertEquals("Sample Item", menuItem.getName().getDefaultTranslation());
        assertEquals(6, menuItem.getDisplayOrder());

        assertEquals("Spaghetti Bolognese", menuItems.get(0).getName().getDefaultTranslation());
        assertEquals(1, menuItems.get(0).getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldHandleMiddleReordering() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setDisplayOrder(3);
        MenuItemFormDTO thirdDisplayOrder = menuItemMapper.toFormDTO(menuItem);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", thirdDisplayOrder);

        Category category = getCategoryById(2L);
        List<MenuItem> menuItems = category.getMenuItems();
        assertEquals(6, menuItems.size());
        menuItem = menuItems.get(2);
        assertEquals("Sample Item", menuItem.getName().getDefaultTranslation());
        assertEquals(3, menuItem.getDisplayOrder());
        assertEquals("Lasagne warzywna", menuItems.get(3).getName().getDefaultTranslation());
        assertEquals(4, menuItems.get(3).getDisplayOrder());

        menuItem.setDisplayOrder(2);
        MenuItemFormDTO secondDisplayOrder = menuItemMapper.toFormDTO(menuItem);

        apiRequestUtils.patchAndExpect200("/api/cms/items/update", secondDisplayOrder);

        category = getCategoryById(2L);
        menuItems = category.getMenuItems();
        menuItem = menuItems.get(1);
        assertEquals("Sample Item", menuItem.getName().getDefaultTranslation());
        assertEquals(2, menuItem.getDisplayOrder());
        assertEquals("Penne Carbonara", menuItems.get(2).getName().getDefaultTranslation());
        assertEquals(3, menuItems.get(2).getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldDeleteMenuItem() throws Exception {
        MenuItemFormDTO menuItem = fetchMenuItemFormDTO(25L);
        assertEquals("Pizza Quattro Formaggi", menuItem.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/items/delete", 25);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/items/show", 25, status().isBadRequest());
        assertEquals("Danie z podanym ID = 25 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldUpdateDisplayOrdersAfterRemoval() throws Exception {
        MenuItemFormDTO menuItem = fetchMenuItemFormDTO(2L);
        assertEquals("Carpaccio z polędwicy wołowej", menuItem.name().defaultTranslation());

        apiRequestUtils.deleteAndExpect200("/api/cms/items/delete", 2);

        Category category = getCategoryById(1L);

        assertEquals(4, category.getMenuItems().size());
        MenuItem secondMenuItem = category.getMenuItems().get(1);
        assertNotEquals("Carpaccio z polędwicy wołowej", secondMenuItem.getName().getDefaultTranslation());
        MenuItem thirdMenuItem = category.getMenuItems().get(2);
        assertEquals("Roladki z bakłażana", thirdMenuItem.getName().getDefaultTranslation());
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToRemoveMenuItem() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/items/delete", 15, status().isForbidden());
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

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }

}