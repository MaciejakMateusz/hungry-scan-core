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
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Order(1)
    void shouldGetAllRestaurants() throws Exception {
        List<Restaurant> restaurants =
                apiRequestUtils.fetchAsList(
                        "/api/cms/restaurants", Restaurant.class);

        assertEquals(2, restaurants.size());
        assertEquals("Rarytas", restaurants.get(0).getName());
        assertEquals("ul. Dębowa 456, Miasteczko, Wiejskie, 98765", restaurants.get(1).getAddress());
    }

    @Test
    @Order(2)
    void shouldNotAllowUnauthorizedAccessToRestaurants() throws Exception {
        mockMvc.perform(get("/api/cms/restaurants")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    @Order(3)
    void shouldShowRestaurantById() throws Exception {
        Restaurant restaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 1, Restaurant.class);
        assertEquals("Rarytas", restaurant.getName());
    }

    @Test
    @Order(4)
    void shouldNotAllowUnauthorizedAccessToShowRestaurant() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/restaurants/show", 2, status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Order(5)
    void shouldNotShowRestaurantById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/restaurants/show", 55, status().isBadRequest());
        assertEquals("Restauracja z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(6)
    void shouldGetNewRestaurantObject() throws Exception {
        Object restaurant = apiRequestUtils.fetchObject("/api/cms/restaurants/add", Restaurant.class);
        assertInstanceOf(Restaurant.class, restaurant);
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(7)
    void shouldNotAllowUnauthorizedAccessToNewRestaurantObject() throws Exception {
        mockMvc.perform(get("/api/cms/restaurants/add")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Order(8)
    void shouldAddNewRestaurant() throws Exception {
        Restaurant restaurant = createRestaurant();

        apiRequestUtils.postAndExpect200("/api/cms/restaurants/add", restaurant);

        Restaurant persistedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 3, Restaurant.class);
        assertEquals("Real Greek Carbonara", persistedRestaurant.getName());
        assertEquals("Korfantego 123", persistedRestaurant.getAddress());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(9)
    void shouldNotAllowUnauthorizedToAddCategory() throws Exception {
        Restaurant restaurant = createRestaurant();
        apiRequestUtils.postAndExpect("/api/cms/restaurants/add", restaurant, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(10)
    void shouldNotAddWithIncorrectName() throws Exception {
        Restaurant restaurant = createRestaurant();
        restaurant.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/restaurants/add", restaurant);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(11)
    void shouldUpdateRestaurant() throws Exception {
        Restaurant existingRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 3, Restaurant.class);
        existingRestaurant.setName("Salty Foots");

        apiRequestUtils.postAndExpect200("/api/cms/restaurants/add", existingRestaurant);

        Restaurant updatedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 3, Restaurant.class);
        assertEquals("Salty Foots", updatedRestaurant.getName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(12)
    void shouldNotAllowUnauthorizedAccessToUpdateRestaurant() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/restaurants/add", new Restaurant(), status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(13)
    void shouldNotUpdateIncorrectRestaurant() throws Exception {
        Restaurant existingRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 3, Restaurant.class);
        existingRestaurant.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/restaurants/add", existingRestaurant);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Order(14)
    void shouldRemoveRestaurant() throws Exception {
        Restaurant existingRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 3, Restaurant.class);

        apiRequestUtils.deleteAndExpect200("/api/cms/restaurants/delete", existingRestaurant);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/restaurants/show", 3, status().isBadRequest());
        assertEquals("Restauracja z podanym ID = 3 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(15)
    void shouldNotAllowUnauthorizedAccessToDeleteRestaurant() throws Exception {
        apiRequestUtils.deleteAndExpect(
                "/api/cms/restaurants/delete", new Restaurant(), status().isForbidden());
    }

    private Restaurant createRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Real Greek Carbonara");
        restaurant.setAddress("Korfantego 123");
        return restaurant;
    }
}
