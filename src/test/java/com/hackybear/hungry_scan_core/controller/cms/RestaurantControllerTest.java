package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestaurantControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void shouldGetAllRestaurants() throws Exception {
        List<Restaurant> restaurants =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/restaurants", Restaurant.class).stream().toList();

        assertEquals(1, restaurants.size());
        assertEquals("Rarytas", restaurants.get(0).getName());
        assertEquals("ul. Główna 123, Miastowo, Województwo, 54321", restaurants.get(0).getAddress());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToRestaurants() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/restaurants");
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    void shouldShowRestaurantById() throws Exception {
        Restaurant restaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 1, Restaurant.class);
        assertEquals("Rarytas", restaurant.getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowRestaurant() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/restaurants/show", 2);
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotShowRestaurantById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/restaurants/show", 55, status().isBadRequest());
        assertEquals("Restauracja z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
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
    void shouldNotAllowUnauthorizedToAddCategory() throws Exception {
        Restaurant restaurant = createRestaurant();
        apiRequestUtils.postAndExpect("/api/cms/restaurants/add", restaurant, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectName() throws Exception {
        Restaurant restaurant = createRestaurant();
        restaurant.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/restaurants/add", restaurant);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdateRestaurant() throws Exception {
        Restaurant existingRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, Restaurant.class);
        existingRestaurant.setName("Salty Foots");

        apiRequestUtils.postAndExpect200("/api/cms/restaurants/add", existingRestaurant);

        Restaurant updatedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, Restaurant.class);
        assertEquals("Salty Foots", updatedRestaurant.getName());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToUpdateRestaurant() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/restaurants/add", new Restaurant(), status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotUpdateIncorrectRestaurant() throws Exception {
        Restaurant existingRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, Restaurant.class);
        existingRestaurant.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/restaurants/add", existingRestaurant);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemoveRestaurant() throws Exception {
        Restaurant restaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, Restaurant.class);
        assertNotNull(restaurant);

        apiRequestUtils.deleteAndExpect200("/api/cms/restaurants/delete", 2);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/restaurants/show", 2, status().isBadRequest());
        assertEquals("Restauracja z podanym ID = 2 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToDeleteRestaurant() throws Exception {
        apiRequestUtils.deleteAndExpect(
                "/api/cms/restaurants/delete", 2, status().isForbidden());
    }

    private Restaurant createRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Real Greek Carbonara");
        restaurant.setAddress("Korfantego 123");
        return restaurant;
    }
}
