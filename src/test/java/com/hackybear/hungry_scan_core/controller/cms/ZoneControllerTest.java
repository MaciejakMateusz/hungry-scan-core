package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.entity.Zone;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.hackybear.hungry_scan_core.utility.Fields.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {STAFF})
    void shouldGetAllZones() throws Exception {
        List<Zone> zones =
                apiRequestUtils.fetchAsList(
                        "/api/cms/zones", Zone.class);
        assertEquals(4, zones.size());
        assertEquals("Piętro II", zones.get(3).getName().getPl());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToZones() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/zones");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldShowZoneById() throws Exception {
        Zone zone = apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 1, Zone.class);
        assertEquals("Sekcja 1", zone.getName().getPl());
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
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
    @WithMockUser(roles = CUSTOMER)
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
        assertEquals("Test zone", persistedZone.getName().getPl());
    }

    @Test
    @WithMockUser(roles = CUSTOMER_READONLY)
    void shouldNotAllowUnauthorizedAccessToAddZone() throws Exception {
        Zone zone = createZone();
        apiRequestUtils.postAndExpect("/api/cms/zones/add", zone, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldNotAddWithIncorrectName() throws Exception {
        Zone zone = createZone();
        zone.setName(getDefaultTranslation(""));

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/zones/add", zone);

        assertEquals(1, errors.size());
        assertEquals("Przynajmniej jedno tłumaczenie nie powinno być puste.", errors.get("name"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateExistingZone() throws Exception {
        Zone persistedZone =
                apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 3, Zone.class);
        assertEquals("Piętro II", persistedZone.getName().getPl());
        persistedZone.setName(getDefaultTranslation("Updated zone"));

        apiRequestUtils.postAndExpect200("/api/cms/zones/add", persistedZone);

        Zone updatedZone =
                apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 3, Zone.class);
        assertEquals("Updated zone", updatedZone.getName().getPl());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Transactional
    @Rollback
    void shouldDeleteZone() throws Exception {
        Zone exitingZone =
                apiRequestUtils.postObjectExpect200("/api/cms/zones/show", 2, Zone.class);
        assertEquals("Sekcja 2", exitingZone.getName().getPl());

        apiRequestUtils.deleteAndExpect200("/api/cms/zones/delete", 2);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/zones/show", 2, status().isBadRequest());
        assertEquals("Strefa z podanym ID = 2 nie istnieje.", responseBody.get("exceptionMsg"));

        List<RestaurantTable> restaurantTables = restaurantTableService.findAll();
        assertEquals(19, restaurantTables.size());
    }

    @Test
    @WithMockUser(roles = CUSTOMER)
    void shouldNotAllowUnauthorizedAccessToRemoveZone() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/zones/delete", 4, status().isForbidden());
    }

    private Zone createZone() {
        Zone zone = new Zone();
        zone.setName(getDefaultTranslation("Test zone"));
        zone.setDisplayOrder(5);
        return zone;
    }

    private Translatable getDefaultTranslation(String value) {
        Translatable translatable = new Translatable();
        translatable.setPl(value);
        return translatable;
    }

}