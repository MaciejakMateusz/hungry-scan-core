package pl.rarytas.rarytas_restaurantside.controller.cms;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Order(1)
    void shouldGetAllMenuItems() throws Exception {
        List<MenuItem> menuItems =
                apiRequestUtils.fetchAsList(
                        "/api/cms/items", MenuItem.class);

        assertEquals(40, menuItems.size());
        assertEquals("Krewetki marynowane w cytrynie", menuItems.get(0).getName());
    }

    @Test
    @Order(2)
    void shouldNotAllowUnauthorizedAccessToMenuItems() throws Exception {
        mockMvc.perform(get("/api/cms/items")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    @Order(3)
    void shouldShowMenuItemById() throws Exception {
        MenuItem menuItem = apiRequestUtils.postObjectExpect200("/api/cms/items/show", 4, MenuItem.class);
        assertEquals("Roladki z bakłażana z feta i suszonymi pomidorami", menuItem.getName());
    }

    @Test
    @Order(4)
    void shouldNotAllowUnauthorizedAccessToShowMenuItem() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/items/show", 4, status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Order(5)
    void shouldNotShowMenuItemById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/items/show", 55, status().isBadRequest());
        assertEquals("Danie z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(6)
    void shouldGetNewMenuItemObject() throws Exception {
        Object menuItem = apiRequestUtils.fetchObject("/api/cms/items/add", MenuItem.class);
        assertInstanceOf(MenuItem.class, menuItem);
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(7)
    void shouldNotAllowUnauthorizedAccessToNewMenuItemObject() throws Exception {
        mockMvc.perform(get("/api/cms/items/add")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(8)
    void shouldAddNewMenuItem() throws Exception {
        MenuItem menuItem = createMenuItem();

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem);

        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 41, MenuItem.class);
        assertEquals("Sample Item", persistedMenuItem.getName());
        assertEquals("Sample description.", persistedMenuItem.getDescription());
        assertEquals(BigDecimal.valueOf(10.99), persistedMenuItem.getPrice());
        assertEquals("/public/assets/sample.png", persistedMenuItem.getImageName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(9)
    void shouldNotAllowUnauthorizedAccessToItemsAdd() throws Exception {
        MenuItem menuItem = createMenuItem();
        apiRequestUtils.postAndExpect("/api/cms/items/add", menuItem, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(10)
    void shouldNotAddWithIncorrectName() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/items/add", menuItem);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(11)
    void shouldNotAddWithIncorrectDescription() throws Exception {
        MenuItem menuItem = createMenuItem();
        menuItem.setDescription("Meme");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/items/add", menuItem);

        assertEquals(1, errors.size());
        assertEquals("Opis kategorii musi mieć minimum 8 znaków", errors.get("description"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(12)
    void shouldUpdateExistingMenuItem() throws Exception {
        MenuItem persistedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 41, MenuItem.class);
        persistedMenuItem.setName("Updated Item");
        persistedMenuItem.setDescription("Updated description.");
        persistedMenuItem.setPrice(BigDecimal.valueOf(15.22));
        persistedMenuItem.setImageName("/public/assets/updated.png");

        apiRequestUtils.postAndExpect200("/api/cms/items/add", persistedMenuItem);

        MenuItem updatedMenuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 41, MenuItem.class);
        assertEquals("Updated Item", updatedMenuItem.getName());
        assertEquals("Updated description.", updatedMenuItem.getDescription());
        assertEquals(BigDecimal.valueOf(15.22), updatedMenuItem.getPrice());
        assertEquals("/public/assets/updated.png", updatedMenuItem.getImageName());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Order(13)
    void shouldDeleteMenuItem() throws Exception {
        MenuItem menuItem =
                apiRequestUtils.postObjectExpect200("/api/cms/items/show", 41, MenuItem.class);
        assertNotNull(menuItem);

        apiRequestUtils.deleteAndExpect200("/api/cms/items/delete", 41);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/items/show", 41, status().isBadRequest());
        assertEquals("Danie z podanym ID = 41 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(15)
    void shouldNotAllowUnauthorizedAccessToRemoveMenuItem() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/items/delete", 15, status().isForbidden());
    }

    private MenuItem createMenuItem() {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Sample Item");
        menuItem.setDescription("Sample description.");
        menuItem.setPrice(BigDecimal.valueOf(10.99));
        menuItem.setImageName("/public/assets/sample.png");
        return menuItem;
    }
}