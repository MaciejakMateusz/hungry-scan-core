package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hackybear.hungry_scan_core.utility.Fields.RESTAURANTS_ALL;
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
    private RestaurantMapper restaurantMapper;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        Objects.requireNonNull(cacheManager.getCache(RESTAURANTS_ALL)).clear();
    }

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldGetAllRestaurants() throws Exception {
        List<RestaurantDTO> restaurants =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/restaurants", RestaurantDTO.class).stream().toList();

        assertEquals(1, restaurants.size());
        assertEquals("Rarytas", restaurants.getFirst().name());
        assertEquals("ul. Główna 123, Miastowo, Województwo, 54321", restaurants.getFirst().address());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToRestaurants() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/restaurants");
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    void shouldShowRestaurantById() throws Exception {
        RestaurantDTO restaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 1, RestaurantDTO.class);
        assertEquals("Rarytas", restaurant.name());
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
        RestaurantDTO restaurantDTO = createRestaurantDTO();

        apiRequestUtils.postAndExpect200("/api/cms/restaurants/add", restaurantDTO);

        RestaurantDTO persistedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 10, RestaurantDTO.class);
        assertEquals("Real Greek Carbonara", persistedRestaurant.name());
        assertEquals("Korfantego 123", persistedRestaurant.address());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedToAddCategory() throws Exception {
        RestaurantDTO restaurantDTO = createRestaurantDTO();
        apiRequestUtils.postAndExpect("/api/cms/restaurants/add", restaurantDTO, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectName() throws Exception {
        Restaurant restaurant = createRestaurant();
        restaurant.setName("");
        RestaurantDTO restaurantDTO = restaurantMapper.toDTO(restaurant);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/restaurants/add", restaurantDTO);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldUpdateRestaurant() throws Exception {
        RestaurantDTO existingRestaurantDTO =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, RestaurantDTO.class);
        Restaurant restaurant = restaurantMapper.toRestaurant(existingRestaurantDTO);
        restaurant.setName("Salty Foots");
        existingRestaurantDTO = restaurantMapper.toDTO(restaurant);

        apiRequestUtils.patchAndExpect200("/api/cms/restaurants/update", existingRestaurantDTO);

        RestaurantDTO updatedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, RestaurantDTO.class);
        assertEquals("Salty Foots", updatedRestaurant.name());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToUpdateRestaurant() throws Exception {
        RestaurantDTO restaurantDTO = createRestaurantDTO();
        apiRequestUtils.patchAndExpectForbidden("/api/cms/restaurants/update", restaurantDTO);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotUpdateIncorrectRestaurant() throws Exception {
        RestaurantDTO existingRestaurantDTO =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, RestaurantDTO.class);
        Restaurant restaurant = restaurantMapper.toRestaurant(existingRestaurantDTO);
        restaurant.setName("");
        existingRestaurantDTO = restaurantMapper.toDTO(restaurant);

        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/cms/restaurants/update", existingRestaurantDTO);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldRemoveRestaurant() throws Exception {
        RestaurantDTO restaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, RestaurantDTO.class);
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

    private RestaurantDTO createRestaurantDTO() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Real Greek Carbonara");
        restaurant.setAddress("Korfantego 123");
        return restaurantMapper.toDTO(restaurant);
    }

}
