package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.MenuColorDTO;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenuColorControllerTest {

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldGetAllMenuColors() throws Exception {
        List<MenuColorDTO> menuColors =
                apiRequestUtils.fetchAsList(
                        "/api/cms/menu-colors", MenuColorDTO.class);

        assertEquals(10, menuColors.size());
        assertTrue(menuColors.stream().anyMatch(menuColor -> "#800000".equals(menuColor.hex())));
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToMenuColors() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/menu-colors");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldShowMenuColorById() throws Exception {
        MenuColorDTO menuColor =
                apiRequestUtils.postObjectExpect200("/api/cms/menu-colors/show", 2, MenuColorDTO.class);
        assertEquals("#003366", menuColor.hex());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowMenuColor() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/menu-colors/show", 5);
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotShowMenuColorById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/menu-colors/show", 78, status().isBadRequest());
        assertEquals("Kolor menu nie istnieje.", responseBody.get("exceptionMsg"));
    }

}