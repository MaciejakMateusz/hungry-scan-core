package pl.rarytas.rarytas_restaurantside.controller.cms;

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
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.Allergen;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllergenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldGetAllAllergens() throws Exception {
        List<Allergen> allergens =
                apiRequestUtils.fetchAsList(
                        "/api/cms/allergens", Allergen.class);

        assertEquals(14, allergens.size());
        assertEquals("Gluten", allergens.get(0).getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAllergens() throws Exception {
        mockMvc.perform(get("/api/cms/allergens")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"})
    void shouldShowAllergenById() throws Exception {
        Allergen allergen =
                apiRequestUtils.postObjectExpect200("/api/cms/allergens/show", 6, Allergen.class);
        assertEquals("Soja", allergen.getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowAllergen() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/allergens/show", 7, status().isUnauthorized());
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