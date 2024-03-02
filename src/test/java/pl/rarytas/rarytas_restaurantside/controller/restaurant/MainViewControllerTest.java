package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser
class MainViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldReturnMainView() throws Exception {
        mockMvc.perform(get("/restaurant"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/main-view"));
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToMainView() throws Exception {
        mockMvc.perform(get("/restaurant"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldReturnMenuView() throws Exception {
        mockMvc.perform(get("/restaurant/menu"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/menu/menu"));
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToMenuView() throws Exception {
        mockMvc.perform(get("/restaurant/menu"))
                .andExpect(status().isForbidden());
    }
}