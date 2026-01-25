package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static com.hackybear.hungry_scan_core.utility.Fields.RESTAURANTS_ALL;
import static com.hackybear.hungry_scan_core.utility.Fields.STAFF;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
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

    @MockitoBean
    private QRService qrService;

    @BeforeEach
    void setUp() throws Exception {
        Objects.requireNonNull(cacheManager.getCache(RESTAURANTS_ALL)).clear();
        Mockito.doNothing().when(qrService).generate(Mockito.any());
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
    @WithMockUser(roles = {STAFF})
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
    @WithMockUser(roles = {STAFF})
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
        RestaurantDTO restaurantDTO = createRestaurantDTO();

        apiRequestUtils.postAndExpect200("/api/cms/restaurants/add", restaurantDTO);

        RestaurantDTO persistedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 13, RestaurantDTO.class);
        assertEquals("Real Greek Carbonara", persistedRestaurant.name());
        assertEquals("Korfantego 123", persistedRestaurant.address());
        assertNotNull(persistedRestaurant.settings());
        assertEquals(1, persistedRestaurant.menus().size());
        assertNotNull(persistedRestaurant.token());

        SettingsDTO settingsDTO = persistedRestaurant.settings();
        assertEquals(7, settingsDTO.operatingHours().size());
        assertEquals(LocalTime.of(11, 0), settingsDTO.operatingHours().get(DayOfWeek.MONDAY).getStartTime());
        assertEquals(LocalTime.of(19, 0), settingsDTO.operatingHours().get(DayOfWeek.MONDAY).getEndTime());
        assertEquals(LocalTime.of(11, 0), settingsDTO.operatingHours().get(DayOfWeek.SUNDAY).getStartTime());
        assertEquals(LocalTime.of(19, 0), settingsDTO.operatingHours().get(DayOfWeek.SUNDAY).getEndTime());

        MenuSimpleDTO menuDTO = persistedRestaurant.menus().stream().findFirst().orElseThrow();
        assertNotNull(menuDTO);
        assertTrue(menuDTO.standard());
        assertEquals(6, menuDTO.plan().size());

        MenuPlanDTO menuPlanDTO = menuDTO.plan()
                .stream()
                .filter(p -> p.dayOfWeek() == DayOfWeek.THURSDAY)
                .findFirst()
                .orElseThrow();
        assertEquals(36, menuPlanDTO.id().toString().length());
        assertNotNull(menuPlanDTO.menuId());

        TimeRange timeRange = menuPlanDTO.timeRanges().stream().findFirst().orElseThrow();
        assertEquals(LocalTime.of(11, 0), timeRange.getStartTime());
        assertEquals(LocalTime.of(19, 0), timeRange.getEndTime());

        assertNotNull(persistedRestaurant.pricePlan());
        assertEquals(1L, persistedRestaurant.pricePlan().getPlanType().getId());
        assertEquals("free", persistedRestaurant.pricePlan().getPlanType().getName());
        assertEquals(BigDecimal.valueOf(0.0), persistedRestaurant.pricePlan().getPlanType().getPrice());
    }

    @Test
    @WithMockUser(roles = STAFF)
    void shouldNotAllowUnauthorizedToAddCategory() throws Exception {
        RestaurantDTO restaurantDTO = createRestaurantDTO();
        apiRequestUtils.postAndExpect("/api/cms/restaurants/add", restaurantDTO, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "fresh@user.it")
    @Transactional
    @Rollback
    void shouldCreateFirstRestaurant() throws Exception {
        User currentUser = userService.findByUsername("fresh@user.it");
        assertNull(currentUser.getActiveRestaurantId());
        RestaurantDTO restaurantDTO = createRestaurantDTO();

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
        assertNotNull(persistedRestaurant.token());
        assertEquals(1, persistedRestaurant.qrVersion());

        SettingsDTO settingsDTO = persistedRestaurant.settings();
        assertEquals(7, settingsDTO.operatingHours().size());
        assertEquals(LocalTime.of(10, 0), settingsDTO.operatingHours().get(DayOfWeek.MONDAY).getStartTime());
        assertEquals(LocalTime.of(22, 0), settingsDTO.operatingHours().get(DayOfWeek.MONDAY).getEndTime());
        assertEquals(LocalTime.of(10, 0), settingsDTO.operatingHours().get(DayOfWeek.SUNDAY).getStartTime());
        assertEquals(LocalTime.of(22, 0), settingsDTO.operatingHours().get(DayOfWeek.SUNDAY).getEndTime());

        MenuSimpleDTO menuDTO = persistedRestaurant.menus().stream().findFirst().orElseThrow();
        assertNotNull(menuDTO);
        assertTrue(menuDTO.standard());

        MenuPlanDTO menuPlanDTO = menuDTO.plan()
                .stream()
                .filter(p -> p.dayOfWeek() == DayOfWeek.THURSDAY)
                .findFirst()
                .orElseThrow();
        assertEquals(36, menuPlanDTO.id().toString().length());
        assertNotNull(menuPlanDTO.menuId());

        TimeRange timeRange = menuPlanDTO.timeRanges().stream().findFirst().orElseThrow();
        assertEquals(LocalTime.of(10, 0), timeRange.getStartTime());
        assertEquals(LocalTime.of(22, 0), timeRange.getEndTime());

        currentUser = userService.findByUsername("fresh@user.it");
        assertNotNull(currentUser.getActiveRestaurantId());
        assertNotNull(currentUser.getActiveMenuId());

        assertNotNull(persistedRestaurant.pricePlan());
        assertEquals(1L, persistedRestaurant.pricePlan().getPlanType().getId());
        assertEquals("free", persistedRestaurant.pricePlan().getPlanType().getName());
        assertEquals(BigDecimal.valueOf(0.0), persistedRestaurant.pricePlan().getPlanType().getPrice());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotAllowToCreateFirstRestaurant() throws Exception {
        RestaurantDTO restaurantDTO = createRestaurantDTO();

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
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldUpdateRestaurant() throws Exception {
        RestaurantDTO existingRestaurantDTO =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, RestaurantDTO.class);
        Restaurant restaurant = restaurantMapper.toRestaurant(existingRestaurantDTO);
        restaurant.setName("Salty Foots");
        Settings settings = restaurant.getSettings();
        settings.setOperatingHours(createOperatingHours());
        restaurant.setSettings(settings);
        existingRestaurantDTO = restaurantMapper.toDTO(restaurant);

        apiRequestUtils.patchAndExpect200("/api/cms/restaurants/update", existingRestaurantDTO);

        RestaurantDTO updatedRestaurant =
                apiRequestUtils.postObjectExpect200("/api/cms/restaurants/show", 2, RestaurantDTO.class);
        assertEquals("Salty Foots", updatedRestaurant.name());

        Set<MenuPlanDTO> menuPlanDTOs = updatedRestaurant.menus()
                .stream()
                .filter(menu -> menu.id().equals(2L))
                .findFirst()
                .orElseThrow()
                .plan();

        TimeRange timeRange = menuPlanDTOs.stream()
                .findFirst()
                .orElseThrow()
                .timeRanges()
                .stream()
                .findFirst()
                .orElseThrow();

        assertEquals(LocalTime.of(11, 0), timeRange.getStartTime());
        assertEquals(LocalTime.of(19, 0), timeRange.getEndTime());

        SettingsDTO settingsDTO = updatedRestaurant.settings();
        assertEquals(7, settingsDTO.operatingHours().size());
        assertFalse(settingsDTO.operatingHours().get(DayOfWeek.MONDAY).isAvailable());
        assertEquals(LocalTime.of(11, 0), settingsDTO.operatingHours().get(DayOfWeek.SUNDAY).getStartTime());
        assertEquals(LocalTime.of(19, 0), settingsDTO.operatingHours().get(DayOfWeek.SUNDAY).getEndTime());
    }

    @Test
    @WithMockUser(roles = STAFF)
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
    @WithMockUser(roles = "MANAGER", username = "netka@test.com")
    @Transactional
    @Rollback
    void shouldRemove() throws Exception {
        apiRequestUtils.deleteAndExpect200("/api/cms/restaurants/delete");

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/restaurants/show", 4, status().isBadRequest());
        assertEquals("Restauracja z podanym ID nie istnieje.", responseBody.get("exceptionMsg"));

        User user = userRepository.findUserByUsername("freeplan@example.com");
        assertEquals(10L, user.getActiveRestaurantId());
        assertEquals(7L, user.getActiveMenuId());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "freeplan@example.com")
    @Transactional
    @Rollback
    void shouldRemoveAsLastOneAssignedToOtherUser() throws Exception {
        Map<?, ?> response = apiRequestUtils.deleteAndReturnResponseBody(
                "/api/cms/restaurants/delete", status().isBadRequest());
        assertEquals("Jedyna restauracja innego użytkownika nie może zostać usunięta.", response.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldNotRemoveWithPaidPlan() throws Exception {
        Map<?, ?> errors = apiRequestUtils.deleteAndReturnResponseBody("/api/cms/restaurants/delete", Map.class, status().isBadRequest());
        String error = (String) errors.get("exceptionMsg");
        assertEquals("Restauracja posiada płatny plan, będzie mogła być usunięta po jego wygaśnięciu.", error);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void shouldNotRemoveWhenOnlyOneRemains() throws Exception {
        Map<?, ?> errors = apiRequestUtils.deleteAndReturnResponseBody("/api/cms/restaurants/delete", Map.class, status().isBadRequest());
        String error = (String) errors.get("exceptionMsg");
        assertEquals("Ostatnia restauracja powiązana do użytkownika nie może zostać usunięta.", error);
    }

    @Test
    @WithMockUser(roles = STAFF)
    void shouldNotAllowUnauthorizedAccessToDeleteRestaurant() throws Exception {
        apiRequestUtils.deleteAndExpect(
                "/api/cms/restaurants/delete", 2, status().isForbidden());
    }

    @Test
    void shouldGetOperatingHours() throws Exception {
        Map<?, ?> operatingHours = apiRequestUtils.fetchObject(
                "/api/cms/restaurants/operating-hours/3d90381d-80d2-48f8-80b3-d237d5f0a8ed", Map.class);
        assertEquals(7, operatingHours.size());
        Map<?, ?> fridayRanges = (Map<?, ?>) operatingHours.get("FRIDAY");
        assertEquals("12:00:00", fridayRanges.get("startTime"));
        assertTrue((Boolean) fridayRanges.get("available"));
    }

    private RestaurantDTO createRestaurantDTO() {
        return restaurantMapper.toDTO(createRestaurant());
    }

    private Restaurant createRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Real Greek Carbonara");
        restaurant.setAddress("Korfantego 123");
        restaurant.setCity("Katowice");
        restaurant.setPostalCode("40-404");
        Settings settings = new Settings();
        settings.setOperatingHours(createOperatingHours());
        settings.setLanguage(Language.PL);
        settings.setSupportedLanguages(Set.of(Language.FR));
        restaurant.setSettings(settings);
        return restaurant;
    }

    private static Map<DayOfWeek, TimeRange> createOperatingHours() {
        Map<DayOfWeek, TimeRange> operatingHours = new HashMap<>();
        Arrays.asList(DayOfWeek.values()).forEach(day -> {
            TimeRange timeRange;
            if (DayOfWeek.MONDAY.equals(day)) {
                timeRange = createTimeRange(false);
            } else {
                timeRange = createTimeRange(true);
            }
            operatingHours.put(day, timeRange);
        });
        return operatingHours;
    }

    private static TimeRange createTimeRange(boolean available) {
        return new TimeRange(LocalTime.of(11, 0), LocalTime.of(19, 0)).withAvailable(available);
    }

}