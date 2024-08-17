package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.entity.Settings;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetSettings() throws Exception {
        Settings settings =
                apiRequestUtils.fetchObject(
                        "/api/admin/settings", Settings.class);

        assertEquals(3, settings.getBookingDuration());
        assertEquals(LocalTime.of(7, 0), settings.getOpeningTime());
        assertEquals(LocalTime.of(23, 0), settings.getClosingTime());
        assertEquals(Language.ENG, settings.getLanguage());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowAccessToSettings() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/settings");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToSettings() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/admin/settings");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void shouldUpdateSettings() throws Exception {
        Settings settings = createSettings();
        apiRequestUtils.patchAndExpect200("/api/admin/settings", settings);

        assertEquals(2, settings.getBookingDuration());
        assertEquals(LocalTime.of(10, 30), settings.getOpeningTime());
        assertEquals(LocalTime.of(22, 0), settings.getClosingTime());
        assertEquals(Language.PL, settings.getLanguage());
        assertEquals((short) 90, settings.getCapacity());
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowAccessToUpdateSettings() throws Exception {
        Settings settings = createSettings();
        apiRequestUtils.patchAndExpect("/api/admin/settings", settings, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUpdateSettings() throws Exception {
        Settings settings = createSettings();
        apiRequestUtils.patchAndExpect("/api/admin/settings", settings, status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotUpdateIncorrectSettings() throws Exception {
        Settings settings = createSettings();
        settings.setClosingTime(null);
        settings.setLanguage(null);

        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/admin/settings", settings);

        assertEquals(2, errors.size());
        assertEquals("Pole nie może być puste", errors.get("closingTime"));
        assertEquals("Pole nie może być puste", errors.get("language"));
    }

    private Settings createSettings() {
        Settings settings = new Settings();
        settings.setId(1);
        settings.setCapacity((short) 90);
        settings.setBookingDuration(2L);
        settings.setLanguage(Language.PL);
        settings.setOpeningTime(LocalTime.of(10, 30));
        settings.setClosingTime(LocalTime.of(22, 0));
        settings.setCustomerSessionTime(3L);
        settings.setEmployeeSessionTime(20L);
        return settings;
    }
}