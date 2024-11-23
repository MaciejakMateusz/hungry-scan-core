package com.hackybear.hungry_scan_core.controller.cms;

import com.hackybear.hungry_scan_core.dto.AllergenDTO;
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
class AllergenControllerTest {

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
    void shouldGetAllAllergens() throws Exception {
        List<AllergenDTO> allergens =
                apiRequestUtils.fetchAsList(
                        "/api/cms/allergens", AllergenDTO.class);

        assertEquals(14, allergens.size());
        assertEquals("Gluten", allergens.getFirst().name().defaultTranslation());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAllergens() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/cms/allergens");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"})
    void shouldShowAllergenById() throws Exception {
        AllergenDTO allergen =
                apiRequestUtils.postObjectExpect200("/api/cms/allergens/show", 6, AllergenDTO.class);
        assertEquals("Soja", allergen.name().defaultTranslation());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowAllergen() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/cms/allergens/show", 7);
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldNotShowAllergenById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/cms/allergens/show", 78, status().isBadRequest());
        assertEquals("Alergen z podanym ID = 78 nie istnieje.", responseBody.get("exceptionMsg"));
    }

}