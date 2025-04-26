package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.enums.Language;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SettingsControllerTest {

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
    void shouldGetSettings() throws Exception {
        SettingsDTO settings =
                apiRequestUtils.fetchObject(
                        "/api/admin/settings", SettingsDTO.class);

        assertEquals(3, settings.bookingDuration());
        assertEquals(LocalTime.of(7, 0), settings.openingTime());
        assertEquals(LocalTime.of(23, 0), settings.closingTime());
        assertEquals(Language.ENG, settings.language());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowAccessToSettings() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/settings");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToSettings() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/settings");
    }
}