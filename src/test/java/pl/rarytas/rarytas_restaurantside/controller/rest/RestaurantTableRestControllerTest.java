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

    @Test
    public void shouldGetAllFromDB() {
        List<RestaurantTable> tables = restaurantTableService.findAll();
        assertEquals(19, tables.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/restaurantTables")).andReturn();
        String actualTableJson = result.getResponse().getContentAsString();
        assertEquals("[{\"id\":1,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":2,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":3,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":4,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":5,\"customerName\":null,\"bookings\":[{\"id\":1,\"date\":\"2024-02-23\",\"time\":\"16:00:00\",\"expirationTime\":\"19:00:00\",\"numOfPpl\":2,\"surname\":\"Pierwszy\",\"tableId\":5,\"numTablesBooked\":null}],\"active\":false},{\"id\":6,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":7,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":8,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":9,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":10,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":11,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":12,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":13,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":14,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":15,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":16,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":17,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":18,\"customerName\":null,\"bookings\":[],\"active\":false},{\"id\":19,\"customerName\":null,\"bookings\":[],\"active\":false}]",
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