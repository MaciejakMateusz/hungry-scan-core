package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.BannerDTO;
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
class BannerControllerTest {

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
    void shouldGetAll() throws Exception {
        List<BannerDTO> labels =
                apiRequestUtils.fetchAsList(
                        "/api/cms/banners", BannerDTO.class);

        assertEquals(3, labels.size());
        assertEquals("Nowość", labels.getFirst().name().pl());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToGetAll() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/banners");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"})
    void shouldShowById() throws Exception {
        BannerDTO banner =
                apiRequestUtils.postObjectExpect200("/api/cms/banners/show", "promo", BannerDTO.class);
        assertEquals("Promocja", banner.name().pl());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowLabel() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/banners/show", "new");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotShowLabelById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/banners/show", "iDoNotExist", status().isBadRequest());
        assertEquals("Nie znaleziono baneru.", responseBody.get("exceptionMsg"));
    }

}