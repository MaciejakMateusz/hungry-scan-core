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
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableServiceInterface;

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
public class TestRestaurantTableRestController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantTableServiceInterface restaurantTableService;

    @Test
    public void shouldGetAllFromDB() {
        List<RestaurantTable> tables = restaurantTableService.findAll();
        assertEquals(19, tables.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/restaurantTables")).andReturn();
        String actualTableJson = result.getResponse().getContentAsString();
        assertEquals("[{\"id\":1,\"customerName\":null},{\"id\":2,\"customerName\":null},{\"id\":3,\"customerName\":null},{\"id\":4,\"customerName\":null},{\"id\":5,\"customerName\":null},{\"id\":6,\"customerName\":null},{\"id\":7,\"customerName\":null},{\"id\":8,\"customerName\":null},{\"id\":9,\"customerName\":null},{\"id\":10,\"customerName\":null},{\"id\":11,\"customerName\":null},{\"id\":12,\"customerName\":null},{\"id\":13,\"customerName\":null},{\"id\":14,\"customerName\":null},{\"id\":15,\"customerName\":null},{\"id\":16,\"customerName\":null},{\"id\":17,\"customerName\":null},{\"id\":18,\"customerName\":null},{\"id\":19,\"customerName\":null}]",
                actualTableJson);
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