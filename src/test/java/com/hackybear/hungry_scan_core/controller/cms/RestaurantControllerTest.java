package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantSimpleDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hackybear.hungry_scan_core.utility.Fields.RESTAURANTS_ALL;
import static org.junit.jupiter.api.Assertions.*;
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
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserRepository userRepository;

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
        List<RestaurantSimpleDTO> restaurants =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/restaurants", RestaurantSimpleDTO.class).stream().toList();

        assertEquals(1, restaurants.size());
        assertEquals("Rarytas", restaurants.getFirst().name());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToRestaurants() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/restaurants");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void shouldFindCurrentRestaurant() throws Exception {
        RestaurantDTO restaurant =
                apiRequestUtils.fetchObject("/api/cms/restaurants/current", RestaurantDTO.class);
        assertEquals("Rarytas", restaurant.name());
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
        assertEquals("Restauracja z podanym ID nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldAddNewRestaurant() throws Exception {
        RestaurantDTO restaurantDTO = createRestaurantDTO(true);

        apiRequestUtils.postAndExpect200("/api/cms/restaurants/add", restaurantDTO);

        RestaurantDTO persistedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 13, RestaurantDTO.class);
        assertEquals("Real Greek Carbonara", persistedRestaurant.name());
        assertEquals("Korfantego 123", persistedRestaurant.address());
        assertNotNull(persistedRestaurant.settings());
        assertEquals(LocalTime.of(12, 0), persistedRestaurant.settings().openingTime());
        assertEquals(LocalTime.of(19, 0), persistedRestaurant.settings().closingTime());
        assertEquals(1, persistedRestaurant.menus().size());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedToAddCategory() throws Exception {
        RestaurantDTO restaurantDTO = createRestaurantDTO(false);
        apiRequestUtils.postAndExpect("/api/cms/restaurants/add", restaurantDTO, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "fresh@user.it")
    @Transactional
    @Rollback
    void shouldCreateFirstRestaurant() throws Exception {
        User currentUser = userService.findByUsername("fresh@user.it");
        assertNull(currentUser.getActiveRestaurantId());
        RestaurantDTO restaurantDTO = createRestaurantDTO(false);

        MockHttpServletResponse response = apiRequestUtils.executePost(
                "/api/cms/restaurants/create-first", restaurantDTO);
        assertEquals(200, response.getStatus());
        assertEquals("{\"redirectUrl\":\"/app\"}", response.getContentAsString());

        RestaurantDTO persistedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 12, RestaurantDTO.class);
        assertEquals("Real Greek Carbonara", persistedRestaurant.name());
        assertEquals("Korfantego 123", persistedRestaurant.address());
        assertEquals(1, persistedRestaurant.menus().size());
        MenuSimpleDTO menuSimpleDTO = persistedRestaurant.menus().stream().findFirst().orElse(null);
        assertNotNull(menuSimpleDTO);
        assertEquals("Menu", menuSimpleDTO.name());

        currentUser = userService.findByUsername("fresh@user.it");
        assertNotNull(currentUser.getActiveRestaurantId());
        assertNotNull(currentUser.getActiveMenuId());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotAllowToCreateFirstRestaurant() throws Exception {
        RestaurantDTO restaurantDTO = createRestaurantDTO(false);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/restaurants/create-first", restaurantDTO);
        assertEquals("Użytkownik utworzył już restaurację.", errors.get("error"));
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
        Settings settings = restaurant.getSettings();
        settings.setClosingTime(LocalTime.of(17, 0));
        restaurant.setSettings(settings);
        existingRestaurantDTO = restaurantMapper.toDTO(restaurant);

        apiRequestUtils.patchAndExpect200("/api/cms/restaurants/update", existingRestaurantDTO);

        RestaurantDTO updatedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, RestaurantDTO.class);
        assertEquals("Salty Foots", updatedRestaurant.name());
        assertEquals(LocalTime.of(17, 0), updatedRestaurant.settings().closingTime());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToUpdateRestaurant() throws Exception {
        RestaurantDTO restaurantDTO = createRestaurantDTO(false);
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
    @WithMockUser(roles = "ADMIN", username = "freeplan@example.com")
    @Transactional
    @Rollback
    void shouldRemove() throws Exception {
        apiRequestUtils.deleteAndExpect200("/api/cms/restaurants/delete");

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/restaurants/show", 10, status().isBadRequest());
        assertEquals("Restauracja z podanym ID nie istnieje.", responseBody.get("exceptionMsg"));
        User user = userRepository.findUserByUsername("freeplan@example.com");
        assertEquals(11L, user.getActiveRestaurantId());
        assertEquals(7L, user.getActiveMenuId());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldNotRemoveWithPaidPlan() throws Exception {
        Map<?, ?> errors = apiRequestUtils.deleteAndReturnResponseBody("/api/cms/restaurants/delete", Map.class, status().isBadRequest());
        String error = (String) errors.get("exceptionMsg");
        assertEquals("Restauracja posiada płatny plan, będzie mogła być usunięta po jego wygaśnięciu.", error);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotRemoveWhenOnlyOneRemains() throws Exception {
        Map<?, ?> errors = apiRequestUtils.deleteAndReturnResponseBody("/api/cms/restaurants/delete", Map.class, status().isBadRequest());
        String error = (String) errors.get("exceptionMsg");
        assertEquals("Ostatnia restauracja powiązana do użytkownika nie może zostać usunięta.", error);
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
        restaurant.setCity("Katowice");
        restaurant.setPostalCode("40-404");
        return restaurant;
    }

    private RestaurantDTO createRestaurantDTO(boolean withSettings) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Real Greek Carbonara");
        restaurant.setAddress("Korfantego 123");
        restaurant.setCity("Katowice");
        restaurant.setPostalCode("40-404");
        if (withSettings) {
            restaurant.setSettings(createSettings());
        }
        return restaurantMapper.toDTO(restaurant);
    }

    private Settings createSettings() {
        Settings settings = new Settings();
        settings.setOpeningTime(LocalTime.of(12, 0));
        settings.setClosingTime(LocalTime.of(19, 0));
        return settings;
    }

}
