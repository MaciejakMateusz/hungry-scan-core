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
import pl.rarytas.rarytas_restaurantside.entity.Label;
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
class LabelControllerTest {

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
    void shouldGetAllLabels() throws Exception {
        List<Label> labels =
                apiRequestUtils.fetchAsList(
                        "/api/cms/labels", Label.class);

        assertEquals(9, labels.size());
        assertEquals("Wegetariańskie / Wegańskie", labels.get(0).getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToLabels() throws Exception {
        mockMvc.perform(get("/api/cms/labels")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"})
    void shouldShowLabelById() throws Exception {
        Label label =
                apiRequestUtils.postObjectExpect200("/api/cms/labels/show", 6, Label.class);
        assertEquals("Tradycyjne / Regionalne", label.getName());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowLabel() throws Exception {
        apiRequestUtils.postAndExpect("/api/cms/labels/show", 7, status().isUnauthorized());
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