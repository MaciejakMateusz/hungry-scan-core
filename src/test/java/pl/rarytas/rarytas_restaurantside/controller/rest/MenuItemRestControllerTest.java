package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MenuItemRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Test
    public void shouldGetAllFromDB() {
        List<MenuItem> menuItems = menuItemService.findAll();
        assertEquals(40, menuItems.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        List<MenuItem> menuItems =
                apiRequestUtils.fetchObjects(
                        "/api/items", MenuItem.class);
        assertEquals(40, menuItems.size());
        assertEquals("Nachos z sosem serowym", menuItems.get(4).getName());
    }

    @Test
    public void shouldGetByIdFromDB() throws LocalizedException {
        MenuItem menuItem = menuItemService.findById(6);
        assertEquals("Spaghetti Bolognese", menuItem.getName());
    }

    @Test
    public void shouldGetByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/items/6")).andReturn();
        String actualMenuItemJson = result.getResponse().getContentAsString();
        assertTrue(actualMenuItemJson.contains("\"name\":\"Spaghetti Bolognese\""));
    }
}
