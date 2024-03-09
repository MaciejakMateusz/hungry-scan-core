package pl.rarytas.rarytas_restaurantside.controller.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(1)
    void shouldGetAllMenuItems() throws Exception {
        List<MenuItem> menuItems =
                apiRequestUtils.fetchObjects(
                        "/api/cms/items", MenuItem.class);

        assertEquals(40, menuItems.size());
        assertEquals("Krewetki marynowane w cytrynie", menuItems.get(0).getName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(2)
    void shouldNotAllowUnauthorizedAccessToMenuItems() throws Exception {
        mockMvc.perform(get("/api/cms/items"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(3)
    void shouldShowMenuItemById() throws Exception {
        MenuItem menuItem = apiRequestUtils.getObjectExpect200("/api/cms/items/show", 4, MenuItem.class);
        assertEquals("Roladki z bakÅ\u0082aÅ¼ana z feta i suszonymi pomidorami", menuItem.getName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(4)
    void shouldNotAllowUnauthorizedAccessToShowMenuItem() throws Exception {
        Integer id = 4;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/cms/items/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(5)
    void shouldNotShowMenuItemById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/items/show", 55, status().isBadRequest());
        assertNotNull(responseBody.get("exceptionMsg"));
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
        mockMvc.perform(get("/api/cms/items/add"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(8)
    void shouldAddNewMenuItem() throws Exception {
        MenuItem menuItem = createMenuItem();

        apiRequestUtils.postAndExpect200("/api/cms/items/add", menuItem, getFile());

        MenuItem persistedMenuItem =
                apiRequestUtils.getObjectExpect200("/api/cms/items/show", 41, MenuItem.class);
        assertEquals("Sample Item", persistedMenuItem.getName());
        assertEquals("Sample description.", persistedMenuItem.getDescription());
        assertEquals(BigDecimal.valueOf(10.99), persistedMenuItem.getPrice());
        assertEquals("sample.png", persistedMenuItem.getImageName());
    }

    private MenuItem createMenuItem() {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Sample Item");
        menuItem.setDescription("Sample description.");
        menuItem.setPrice(BigDecimal.valueOf(10.99));
        return menuItem;
    }

    private MockMultipartFile getFile() throws IOException {
        Path path = Paths.get("src/test/resources/images/sample.png");
        byte[] pngBytes = Files.readAllBytes(path);

        return new MockMultipartFile(
                "file",
                "sample.png",
                MediaType.IMAGE_PNG_VALUE,
                pngBytes
        );
    }
}