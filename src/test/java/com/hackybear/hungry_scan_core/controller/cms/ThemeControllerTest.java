package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.entity.Theme;
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
class ThemeControllerTest {

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
    void shouldGetAllThemes() throws Exception {
        List<Theme> themes =
                apiRequestUtils.fetchAsList(
                        "/api/cms/themes", Theme.class);

        assertEquals(4, themes.size());
        assertEquals("pink", themes.get(1).getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToThemes() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/themes");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void shouldShowThemeById() throws Exception {
        Theme theme = apiRequestUtils.postObjectExpect200("/api/cms/themes/show", 4, Theme.class);
        assertEquals("orange", theme.getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowMenuItem() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/themes/show", 3);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotShowThemeById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/themes/show", 55, status().isBadRequest());
        assertEquals("Motyw z ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    @Transactional
    @Rollback
    void shouldSwitchActiveTheme() throws Exception {
        Theme theme1 = apiRequestUtils.postObjectExpect200("/api/cms/themes/show", 1, Theme.class);
        assertTrue(theme1.isActive());

        apiRequestUtils.postAndExpect200("/api/cms/themes/set-active", 3);

        theme1 = apiRequestUtils.postObjectExpect200("/api/cms/themes/show", 1, Theme.class);
        assertFalse(theme1.isActive());

        Theme theme3 =
                apiRequestUtils.postObjectExpect200("/api/cms/themes/show", 3, Theme.class);
        assertTrue(theme3.isActive());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldNotAllowForbiddenAccessToEndpoints() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/themes");
        apiRequestUtils.postAndExpect("/api/cms/themes/set-active", 3, status().isForbidden());
        apiRequestUtils.postAndExpect("/api/cms/themes/show", 1, status().isForbidden());
    }

}