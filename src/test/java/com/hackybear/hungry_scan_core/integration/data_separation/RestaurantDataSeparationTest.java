package com.hackybear.hungry_scan_core.integration.data_separation;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantSimpleDTO;
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

import static com.hackybear.hungry_scan_core.utility.Fields.STAFF;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestaurantDataSeparationTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

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
    void getRestaurants_1stUser() throws Exception {
        List<RestaurantSimpleDTO> restaurants =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/restaurants", RestaurantSimpleDTO.class).stream().toList();

        assertEquals(1, restaurants.size());
        assertEquals("Rarytas", restaurants.getFirst().name());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "restaurator@rarytas.pl")
    @Transactional
    @Rollback
    void getRestaurants_2ndUser() throws Exception {
        List<RestaurantSimpleDTO> restaurants =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/restaurants", RestaurantSimpleDTO.class).stream().toList();

        assertEquals(1, restaurants.size());
        assertEquals("Wykwintna Bistro", restaurants.getFirst().name());
    }

    @Test
    @WithMockUser(roles = {STAFF}, username = "kucharz@antek.pl")
    @Transactional
    @Rollback
    void getAllowedUserRestaurants_1stUser() throws Exception {
        List<RestaurantDTO> restaurants =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/restaurants", RestaurantDTO.class).stream().toList();

        assertEquals(2, restaurants.size());
        assertEquals("Test 1", restaurants.getFirst().name());
        assertEquals("Wykwintna Bistro", restaurants.get(1).name());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"}, username = "netka@test.com")
    @Transactional
    @Rollback
    void getAllowedUserRestaurants_2ndUser() throws Exception {
        List<RestaurantDTO> restaurants =
                apiRequestUtils.fetchAsSet(
                        "/api/cms/restaurants", RestaurantDTO.class).stream().toList();

        assertEquals(3, restaurants.size());
        assertEquals("Rarytas", restaurants.getFirst().name());
        assertEquals("Test 2", restaurants.get(1).name());
        assertEquals("Test 3", restaurants.get(2).name());

    }

}
