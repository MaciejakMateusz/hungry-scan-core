package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.LabelDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LabelControllerTest {

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
    void shouldGetAllLabels() throws Exception {
        List<LabelDTO> labels =
                apiRequestUtils.fetchAsList(
                        "/api/cms/labels", LabelDTO.class);

        assertEquals(6, labels.size());
        assertEquals("Bez glutenu", labels.getFirst().name().defaultTranslation());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToLabels() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/labels");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"})
    void shouldShowLabelById() throws Exception {
        LabelDTO label =
                apiRequestUtils.postObjectExpect200("/api/cms/labels/show", 6, LabelDTO.class);
        assertEquals("Ostre", label.name().defaultTranslation());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowLabel() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/labels/show", 7);
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotShowLabelById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/labels/show", 78, status().isBadRequest());
        assertEquals("Etykieta z podanym ID = 78 nie istnieje.", responseBody.get("exceptionMsg"));
    }

}