package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private MenuItemFactory menuItemFactory;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldGetAllMenuItems() throws Exception {
        List<MenuItem> menuItems =
                apiRequestUtils.fetchAsList(
                        "/api/cms/items", MenuItem.class);

        assertEquals(33, menuItems.size());
        assertEquals("Krewetki marynowane w cytrynie", menuItems.get(0).getName().getDefaultTranslation());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToMenuItems() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/cms/items");
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    void shouldShowMenuItemById() throws Exception {
        MenuItem menuItem = apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItem.class);
        assertEquals("Roladki z bakłażana", menuItem.getName().getDefaultTranslation());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowMenuItem() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/items/show", 4, status().isUnauthorized());
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
    void shouldGetNewMenuItemObject() throws Exception {
        Object menuItem = apiRequestUtils.fetchObject("/api/cms/items/add", MenuItem.class);
        assertInstanceOf(MenuItem.class, menuItem);
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToNewMenuItemObject() throws Exception {
        mockMvc.perform(get("/api/cms/items/add")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewMenuItem() throws Exception {
        MenuItem menuItem = createMenuItem();

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem);

        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200(
                        "/api/cms/items/show", 36, MenuItem.class);

        assertEquals("Sample Item", persistedMenuItem.getName().getDefaultTranslation());
        assertEquals("Sample description", persistedMenuItem.getDescription().getDefaultTranslation());
        assertEquals(5, persistedMenuItem.getIngredients().size());
        assertEquals(5, persistedMenuItem.getAdditionalIngredients().size());
        assertEquals(1, persistedMenuItem.getAllergens().size());
        assertEquals(2, persistedMenuItem.getLabels().size());
        assertEquals("/public/assets/sample.png", persistedMenuItem.getImageName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToItemsAdd() throws Exception {
        MenuItem menuItem = createMenuItem();
        apiRequestUtils.postAndExpect("/api/cms/items/add", menuItem, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldNotAddWithIncorrectName() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setName(getDefaultTranslation(""));

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/items/add", menuItem);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateExistingMenuItem() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 23, MenuItem.class);
        persistedMenuItem.setName(getDefaultTranslation("Updated Item"));
        persistedMenuItem.setDescription(getDefaultTranslation("Updated Description"));
        persistedMenuItem.setImageName("/public/assets/updated.png");

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 23, MenuItem.class);
        assertEquals("Updated Item", updatedMenuItem.getName().getDefaultTranslation());
        assertEquals("Updated Description", updatedMenuItem.getDescription().getDefaultTranslation());
        assertEquals("/public/assets/updated.png", updatedMenuItem.getImageName());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToBigger() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItem.class);
        assertEquals(1, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(3);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItem.class);
        assertEquals(3, updatedMenuItem.getDisplayOrder());

        MenuItem thirdToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 3, MenuItem.class);
        assertEquals(2, thirdToSecond.getDisplayOrder());

        MenuItem secondToFirst =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 2, MenuItem.class);
        assertEquals(1, secondToFirst.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToLower() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItem.class);
        assertEquals(5, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(2);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItem.class);
        assertEquals(2, updatedMenuItem.getDisplayOrder());

        MenuItem secondToThird =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 2, MenuItem.class);
        assertEquals(3, secondToThird.getDisplayOrder());

        MenuItem fourthToFifth =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItem.class);
        assertEquals(5, fourthToFifth.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToLastWithTooBigValue() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItem.class);
        assertEquals(1, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(15);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItem.class);
        assertEquals(5, updatedMenuItem.getDisplayOrder());

        MenuItem fifthToFourth =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItem.class);
        assertEquals(4, fifthToFourth.getDisplayOrder());

        MenuItem thirdToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 3, MenuItem.class);
        assertEquals(2, thirdToSecond.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderToFirstWithTooLowValue() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 3, MenuItem.class);
        assertEquals(3, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(-2);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 3, MenuItem.class);
        assertEquals(1, updatedMenuItem.getDisplayOrder());

        MenuItem firstToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItem.class);
        assertEquals(2, firstToSecond.getDisplayOrder());

        MenuItem secondToThird =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 2, MenuItem.class);
        assertEquals(3, secondToThird.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderFromLastToFirst() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItem.class);
        assertEquals(5, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(1);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItem.class);
        assertEquals(1, updatedMenuItem.getDisplayOrder());

        MenuItem firstToSecond =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItem.class);
        assertEquals(2, firstToSecond.getDisplayOrder());

        MenuItem fourthToFifth =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItem.class);
        assertEquals(5, fourthToFifth.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldChangeDisplayOrderFromFirstToLast() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItem.class);
        assertEquals(1, persistedMenuItem.getDisplayOrder());
        persistedMenuItem.setDisplayOrder(5);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 1, MenuItem.class);
        assertEquals(5, updatedMenuItem.getDisplayOrder());

        MenuItem secondToFirst =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 2, MenuItem.class);
        assertEquals(1, secondToFirst.getDisplayOrder());

        MenuItem fifthToFourth =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 5, MenuItem.class);
        assertEquals(4, fifthToFourth.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewWithExistingDisplayOrder() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setDisplayOrder(2);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem);

        Category category = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 2, Category.class);
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
                9,
                Money.of(23.55));
        menuItem.setDisplayOrder(22);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem);

        MenuItem persistedMenuItem = apiRequestUtils.postObjectExpect200(
                "/api/cms/items/show", 37, MenuItem.class);
        assertEquals("Sample Item", persistedMenuItem.getName().getDefaultTranslation());
        assertEquals(1, persistedMenuItem.getDisplayOrder());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldHandleBoundaryDisplayOrders() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setDisplayOrder(Integer.MIN_VALUE);

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem);

        Category category = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 2, Category.class);
        List<MenuItem> menuItems = category.getMenuItems();
        assertEquals(6, menuItems.size());
        menuItem = menuItems.get(0);
        assertEquals("Sample Item", menuItem.getName().getDefaultTranslation());
        assertEquals(1, menuItem.getDisplayOrder());
        assertEquals("Spaghetti Bolognese", menuItems.get(1).getName().getDefaultTranslation());
        assertEquals(2, menuItems.get(1).getDisplayOrder());

        menuItem.setDisplayOrder(Integer.MAX_VALUE);
        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem);

        category = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 2, Category.class);
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

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem);

        Category category = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 2, Category.class);
        List<MenuItem> menuItems = category.getMenuItems();
        assertEquals(6, menuItems.size());
        menuItem = menuItems.get(2);
        assertEquals("Sample Item", menuItem.getName().getDefaultTranslation());
        assertEquals(3, menuItem.getDisplayOrder());
        assertEquals("Lasagne warzywna", menuItems.get(3).getName().getDefaultTranslation());
        assertEquals(4, menuItems.get(3).getDisplayOrder());

        menuItem.setDisplayOrder(2);
        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem);

        category = apiRequestUtils.postObjectExpect200(
                "/api/cms/categories/show", 2, Category.class);
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
        MenuItem menuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 25, MenuItem.class);
        assertNotNull(menuItem);

        apiRequestUtils.deleteAndExpect200("/api/cms/items/delete", 25);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/items/show", 25, status().isBadRequest());
        assertEquals("Danie z podanym ID = 25 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToRemoveMenuItem() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/items/delete", 15, status().isForbidden());
    }

    private MenuItem createMenuItem() throws LocalizedException {
        return menuItemFactory.createMenuItem(
                "Sample Item",
                "Sample description",
                2,
                Money.of(23.55));
    }

    private Translatable getDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        return translatable;
    }

}