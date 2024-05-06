package pl.rarytas.hungry_scan_core.controller.cms;

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
import pl.rarytas.hungry_scan_core.entity.MenuItem;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.test_utils.ApiRequestUtils;
import pl.rarytas.hungry_scan_core.test_utils.MenuItemFactory;
import pl.rarytas.hungry_scan_core.utility.Money;

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

        assertEquals(30, menuItems.size());
        assertEquals("Krewetki marynowane w cytrynie", menuItems.get(0).getName());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldGetAllMenuItemsByCategoryId() throws Exception {
        List<MenuItem> menuItems =
                apiRequestUtils.postAndGetList(
                        "/api/cms/items/by-category", 3, MenuItem.class);

        assertEquals(5, menuItems.size());
        assertEquals("Sałatka grecka", menuItems.get(0).getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToMenuItems() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/cms/items");
        apiRequestUtils.postAndExpectUnauthorized("/api/cms/items/by-category", 3);
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    void shouldShowMenuItemById() throws Exception {
        MenuItem menuItem = apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItem.class);
        assertEquals("Roladki z bakłażana z feta i suszonymi pomidorami", menuItem.getName());
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
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 31, MenuItem.class);

        assertEquals("Sample Item", persistedMenuItem.getName());
        assertEquals("Sample description", persistedMenuItem.getDescription());
        assertEquals(5, persistedMenuItem.getIngredients().size());
        assertEquals(5, persistedMenuItem.getAdditionalIngredients().size());
        assertEquals(1, persistedMenuItem.getAllergens().size());
        assertEquals(2, persistedMenuItem.getLabels().size());
        assertEquals(2, persistedMenuItem.getVariants().size());
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
        menuItem.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/items/add", menuItem);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldNotAddWithIncorrectDescription() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setDescription("Meme");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/items/add", menuItem);

        assertEquals(1, errors.size());
        assertEquals("Opis kategorii musi mieć minimum 8 znaków", errors.get("description"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateExistingMenuItem() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 23, MenuItem.class);
        persistedMenuItem.setName("Updated Item");
        persistedMenuItem.setDescription("Updated description.");
        persistedMenuItem.setImageName("/public/assets/updated.png");

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 23, MenuItem.class);
        assertEquals("Updated Item", updatedMenuItem.getName());
        assertEquals("Updated description.", updatedMenuItem.getDescription());
        assertEquals("/public/assets/updated.png", updatedMenuItem.getImageName());
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
                Money.of(23.55));
    }

}