package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;
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
import java.util.UUID;

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
class RestaurantTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldGetAllTables() throws Exception {
        List<RestaurantTable> restaurantTables =
                apiRequestUtils.fetchAsList(
                        "/api/cms/tables", RestaurantTable.class);

        assertEquals(19, restaurantTables.size());
        assertEquals("19436a86-e200-400d-aa2e-da4686805d00", restaurantTables.getFirst().getToken());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowUnauthorizedAccessToTables() throws Exception {
        mockMvc.perform(get("/api/cms/tables")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldShowTableById() throws Exception {
        RestaurantTable restaurantTable =
                apiRequestUtils.postObjectExpect200("/api/cms/tables/show", 6, RestaurantTable.class);
        assertEquals("59ebc00c-b580-4dff-9788-2df90b1d4bba", restaurantTable.getToken());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotAllowUnauthorizedAccessToShowTable() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/tables/show", 7, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotShowTableById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/tables/show", 341, status().isBadRequest());
        assertEquals("Stolik z ID = 341 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldGetNewTableObject() throws Exception {
        Object table = apiRequestUtils.fetchObject("/api/cms/tables/add", RestaurantTable.class);
        assertInstanceOf(RestaurantTable.class, table);
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowUnauthorizedAccessToTableObject() throws Exception {
        mockMvc.perform(get("/api/cms/tables/add")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldAddNewTable() throws Exception {
        RestaurantTable restaurantTable = createTable();

        apiRequestUtils.postAndExpect200("/api/cms/tables/add", restaurantTable);

        RestaurantTable persistedTable =
                apiRequestUtils.postObjectExpect200("/api/cms/tables/show", 20, RestaurantTable.class);
        assertEquals(restaurantTable.getToken(), persistedTable.getToken());
        assertEquals(10, persistedTable.getMaxNumOfPpl());
        assertEquals(24, persistedTable.getNumber());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToAddTable() throws Exception {
        RestaurantTable restaurantTable = createTable();
        apiRequestUtils.postAndExpect("/api/cms/tables/add", restaurantTable, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldNotAddWithIncorrectFields() throws Exception {
        RestaurantTable restaurantTable = createTable();
        restaurantTable.setMaxNumOfPpl(0);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/cms/tables/add", restaurantTable);

        assertEquals(1, errors.size());
        assertEquals("Wartość musi być równa lub większa od 1.", errors.get("maxNumOfPpl"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldUpdateExistingTable() throws Exception {
        RestaurantTable existingTable =
                apiRequestUtils.postObjectExpect200("/api/cms/tables/show", 6, RestaurantTable.class);
        assertEquals("59ebc00c-b580-4dff-9788-2df90b1d4bba", existingTable.getToken());

        existingTable.setMaxNumOfPpl(7);
        existingTable.setNumber(51);

        apiRequestUtils.postAndExpect200("/api/cms/tables/add", existingTable);

        RestaurantTable restaurantTable =
                apiRequestUtils.postObjectExpect200("/api/cms/tables/show", 6, RestaurantTable.class);
        assertEquals("59ebc00c-b580-4dff-9788-2df90b1d4bba", restaurantTable.getToken());
        assertEquals(7, restaurantTable.getMaxNumOfPpl());
        assertEquals(51, restaurantTable.getNumber());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    @Rollback
    void shouldDeleteTable() throws Exception {
        RestaurantTable existingTable =
                apiRequestUtils.postObjectExpect200("/api/cms/tables/show", 6, RestaurantTable.class);
        assertEquals("59ebc00c-b580-4dff-9788-2df90b1d4bba", existingTable.getToken());

        apiRequestUtils.deleteAndExpect200("/api/cms/tables/delete", 6);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/tables/show", 6, status().isBadRequest());
        assertEquals("Stolik z ID = 6 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowUnauthorizedAccessToRemoveTable() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/cms/tables/delete", 12, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotGenerateQrForNonExistingTable() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/tables/generate-qr", 982, status().isBadRequest());
        assertEquals("Stolik z ID = 982 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotGenerateQrWhenTableActivated() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/toggle", 9);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/tables/generate-qr", 9, status().isBadRequest());
        assertEquals("Stolik z ID = 9 jest jeszcze aktywny.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowUnauthorizedAccessToGenerateQr() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/tables/generate-qr", 12, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotDownloadForNonExistingTable() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/tables/download", 982, status().isBadRequest());
        assertEquals("Stolik z ID = 982 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotDownloadForNullQrName() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/tables/download", 4, status().isBadRequest());
        assertEquals("Nie znaleziono pliku z podaną ścieżką: ./src/test/files/qr/null", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowUnauthorizedAccessToDownload() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/tables/download", 12, status().isForbidden());
    }

    private RestaurantTable createTable() {
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setToken(String.valueOf(UUID.randomUUID()));
        restaurantTable.setNumber(24);
        restaurantTable.setMaxNumOfPpl(10);
        return restaurantTable;
    }
}