package pl.rarytas.rarytas_restaurantside.controller.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.Settings;
import pl.rarytas.rarytas_restaurantside.enums.Language;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @Order(1)
    void shouldGetSettings() throws Exception {
        Settings settings =
                apiRequestUtils.fetchObject(
                        "/api/cms/settings", Settings.class);

        assertEquals(3, settings.getBookingDuration());
        assertEquals(LocalTime.of(7, 0), settings.getOpeningTime());
        assertEquals(LocalTime.of(23, 0), settings.getClosingTime());
        assertEquals(Language.ENG, settings.getLanguage());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Order(2)
    void shouldNotAllowUnauthorizedAccessToSettings() throws Exception {
        mockMvc.perform(get("/api/cms/settings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(3)
    void shouldUpdateSettings() throws Exception {
        Settings settings = createSettings();
        apiRequestUtils.patchAndExpect200("/api/cms/settings", settings);

        assertEquals(2, settings.getBookingDuration());
        assertEquals(LocalTime.of(10, 30), settings.getOpeningTime());
        assertEquals(LocalTime.of(22, 0), settings.getClosingTime());
        assertEquals(Language.PL, settings.getLanguage());
        assertEquals((short) 90, settings.getCapacity());
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(4)
    void shouldNotAllowUnauthorizedAccessToUpdateSettings() throws Exception {
        Settings settings = createSettings();
        ObjectMapper objectMapper = apiRequestUtils.prepObjMapper();
        mockMvc.perform(patch("/api/cms/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(5)
    void shouldNotUpdateIncorrectSettings() throws Exception {
        Settings settings = createSettings();
        settings.setClosingTime(null);
        settings.setLanguage(null);

        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/cms/settings", settings);

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
        return settings;
    }
}