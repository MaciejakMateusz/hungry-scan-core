package pl.rarytas.rarytas_restaurantside.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testRegisterGet() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testRegisterPost() throws Exception {
        User user = createCorrectUser();

        mockMvc.perform(post("/register")
                        .param("username", user.getUsername())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .param("repeatedPassword", user.getRepeatedPassword()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("success-registration"));

        user = createIncorrectUser();

        mockMvc.perform(post("/register")
                        .param("username", user.getUsername())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .param("repeatedPassword", user.getRepeatedPassword()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testAdminRegisterPost() throws Exception {
        User admin = createCorrectAdmin();

        mockMvc.perform(post("/registerAdmin")
                        .param("username", admin.getUsername())
                        .param("email", admin.getEmail())
                        .param("password", admin.getPassword())
                        .param("repeatedPassword", admin.getRepeatedPassword()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("success-registration"));

        admin = createIncorrectUser();

        mockMvc.perform(post("/registerAdmin")
                        .param("username", admin.getUsername())
                        .param("email", admin.getEmail())
                        .param("password", admin.getPassword())
                        .param("repeatedPassword", admin.getRepeatedPassword()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registerAdmin"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("user"));
    }

    private User createCorrectUser() {
        User user = new User();
        user.setEmail("example@example.com");
        user.setUsername("exampleUser");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        return user;
    }

    private User createCorrectAdmin() {
        User user = new User();
        user.setEmail("admin@admino.com");
        user.setUsername("exampleAdmin");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        return user;
    }

    private User createIncorrectUser() {
        User user = new User();
        user.setEmail("example@example");
        user.setUsername("ex");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Exhale1!");
        return user;
    }
}