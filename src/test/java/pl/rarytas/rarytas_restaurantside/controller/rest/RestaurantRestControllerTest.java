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
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;
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
public class RestaurantRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Test
    public void shouldGetAllFromDB() {
        List<Restaurant> restaurants = restaurantService.findAll();
        assertEquals(2, restaurants.size());
    }

    @Test
    public void shouldGetAllFromEndpoint() throws Exception {
        List<Restaurant> restaurants =
                apiRequestUtils.fetchObjects(
                        "/api/restaurants", Restaurant.class);

        assertEquals(2, restaurants.size());
        assertEquals("admin", restaurants.get(1).getName());
    }

    @Test
    public void shouldGetByIdFromDB() {
        Restaurant restaurant = restaurantService.findById(1).orElse(new Restaurant());
        assertEquals("Rarytas", restaurant.getName());
    }

    @Test
    public void shouldGetByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/restaurants/2")).andReturn();
        String actualRestaurantJson = result.getResponse().getContentAsString();
        assertTrue(actualRestaurantJson.contains("\"name\":\"Wykwintna Bistro\""));
    }
}
