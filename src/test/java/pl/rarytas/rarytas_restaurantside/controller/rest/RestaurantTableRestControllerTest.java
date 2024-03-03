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
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
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
public class RestaurantTableRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Test
    public void shouldGetAllFromDB() {
        List<RestaurantTable> tables = restaurantTableService.findAll();
        assertEquals(19, tables.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        List<RestaurantTable> restaurantTables =
                apiRequestUtils.fetchObjects(
                        "/api/restaurantTables", RestaurantTable.class);

        assertEquals(19, restaurantTables.size());
    }

    @Test
    public void shouldGetByIdFromDB() {
        RestaurantTable table = restaurantTableService.findById(5).orElse(new RestaurantTable());
        assertEquals(5, table.getId());
    }

    @Test
    public void shouldGetByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/restaurantTables/5")).andReturn();
        String actualTableJson = result.getResponse().getContentAsString();
        assertTrue(actualTableJson.contains("\"id\":5"));
    }
}