package pl.rarytas.rarytas_restaurantside.controller.cms;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.Zone;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private RestaurantTableService restaurantTableService;


    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldGetAllZones() throws Exception {
        List<Zone> zones =
                apiRequestUtils.fetchAsList(
                        "/api/cms/zones", Zone.class);
        assertEquals(4, zones.size());
        assertEquals("Piętro II", zones.get(3).getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToZones() throws Exception {
        mockMvc.perform(get("/api/cms/zones")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldShowZoneById() throws Exception {
        Zone zone = apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 1, Zone.class);
        assertEquals("Sekcja 1", zone.getName());
    }

    @Test
    @WithMockUser(roles = {"COOK"})
    void shouldNotAllowUnauthorizedAccessToShowZone() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/zones/show", 4, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotShowZoneById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/zones/show", 55, status().isBadRequest());
        assertEquals("Strefa z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldGetNewZoneObject() throws Exception {
        Object zone = apiRequestUtils.fetchObject("/api/cms/zones/add", Zone.class);
        assertInstanceOf(Zone.class, zone);
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToNewZoneObject() throws Exception {
        mockMvc.perform(get("/api/cms/zones/add")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewZone() throws Exception {
        Zone zone = createZone();

        apiRequestUtils.postAndExpect200("/api/cms/zones/add", zone);

        Zone persistedZone =
                apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 5, Zone.class);
        assertEquals("Test zone", persistedZone.getName());
        assertEquals(4, persistedZone.getRestaurantTables().size());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToAddZone() throws Exception {
        Zone zone = createZone();
        apiRequestUtils.postAndExpect("/api/cms/zones/add", zone, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldNotAddWithIncorrectName() throws Exception {
        Zone zone = createZone();
        zone.setName("");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/zones/add", zone);

        assertEquals(1, errors.size());
        assertEquals("Pole nie może być puste", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateExistingZone() throws Exception {
        Zone persistedZone =
                apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 3, Zone.class);
        assertEquals("Piętro II", persistedZone.getName());
        persistedZone.setName("Updated zone");

        apiRequestUtils.postAndExpect200("/api/cms/zones/add", persistedZone);

        Zone updatedZone =
                apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 3, Zone.class);
        assertEquals("Updated zone", updatedZone.getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldDeleteZone() throws Exception {
        Zone exitingZone =
                apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 2, Zone.class);
        assertEquals("Sekcja 2", exitingZone.getName());

        apiRequestUtils.deleteAndExpect200("/api/cms/zones/delete", 2);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/zones/show", 2, status().isBadRequest());
        assertEquals("Strefa z podanym ID = 2 nie istnieje.", responseBody.get("exceptionMsg"));

        List<RestaurantTable> restaurantTables = restaurantTableService.findAll();
        assertEquals(19, restaurantTables.size());
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToRemoveZone() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/zones/delete", 4, status().isForbidden());
    }

    private Zone createZone() throws LocalizedException {
        Zone zone = new Zone();
        zone.setName("Test zone");
        zone.setDisplayOrder(5);
        zone.addRestaurantTable(restaurantTableService.findById(1));
        zone.addRestaurantTable(restaurantTableService.findById(4));
        zone.addRestaurantTable(restaurantTableService.findById(8));
        zone.addRestaurantTable(restaurantTableService.findById(12));
        return zone;
    }

}