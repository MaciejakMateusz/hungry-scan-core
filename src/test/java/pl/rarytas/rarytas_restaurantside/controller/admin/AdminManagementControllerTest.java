package pl.rarytas.rarytas_restaurantside.controller.admin;

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
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;
import pl.rarytas.rarytas_restaurantside.testSupport.UserBuilder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminManagementControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(1)
    public void shouldGetAllUsers() throws Exception {
        List<User> users =
                apiRequestUtils.fetchObjects(
                        "/api/admin/users", User.class);

        assertEquals(5, users.size());
        assertEquals("mati", users.get(0).getUsername());
        assertEquals("kucharz@antek.pl", users.get(3).getEmail());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(2)
    void shouldNotAllowUnauthorizedAccessToUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(3)
    public void shouldGetWaiters() throws Exception {
        List<User> users =
                apiRequestUtils.fetchObjects(
                        "/api/admin/users/waiters", User.class);

        assertEquals(2, users.size());
        assertEquals("neta", users.get(1).getUsername());
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(4)
    void shouldNotAllowUnauthorizedAccessToWaiters() throws Exception {
        mockMvc.perform(get("/api/admin/users/waiters"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(5)
    public void shouldGetCooks() throws Exception {
        List<User> users =
                apiRequestUtils.fetchObjects(
                        "/api/admin/users/cooks", User.class);

        assertEquals(1, users.size());
        assertEquals("kucharz", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(6)
    void shouldNotAllowUnauthorizedAccessToCooks() throws Exception {
        mockMvc.perform(get("/api/admin/users/waiters"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(7)
    public void shouldGetManagers() throws Exception {
        List<User> users = apiRequestUtils.fetchObjects(
                "/api/admin/users/managers", User.class);

        assertEquals(2, users.size());
        assertEquals("neta", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(8)
    void shouldNotAllowUnauthorizedAccessToManagers() throws Exception {
        mockMvc.perform(get("/api/admin/users/managers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(9)
    public void shouldGetAdmins() throws Exception {
        List<User> users = apiRequestUtils.fetchObjects(
                "/api/admin/users/admins", User.class);

        assertEquals(2, users.size());
        assertEquals("admin", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(10)
    void shouldNotAllowUnauthorizedAccessToAdmins() throws Exception {
        mockMvc.perform(get("/api/admin/users/admins"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(11)
    void shouldShowUserById() throws Exception {
        User user = apiRequestUtils.getObjectExpect200("/api/admin/users/show", 5, User.class);
        assertEquals("owner", user.getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(12)
    void shouldNotAllowUnauthorizedAccessToShowUser() throws Exception {
        Integer id = 4;
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/admin/users/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(id)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(13)
    void shouldNotShowUserById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/show", 55, status().isBadRequest());
        assertNotNull(responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(14)
    void shouldGetNewUserObject() throws Exception {
        Object user = apiRequestUtils.fetchObject("/api/admin/users/add", User.class);
        assertInstanceOf(User.class, user);
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(15)
    void shouldNotAllowUnauthorizedAccessToNewUserObject() throws Exception {
        mockMvc.perform(get("/api/admin/users/add"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(16)
    void shouldAddNewUser() throws Exception {
        User user = UserBuilder.createUser();
        apiRequestUtils.postAndExpect200("/api/admin/users/add", user);

        User persistedUser = apiRequestUtils.getObjectExpect200("/api/admin/users/show", 6, User.class);
        assertEquals("exampleUser", persistedUser.getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(17)
    void shouldNotAllowUnauthorizedToAddUser() throws Exception {
        User user = UserBuilder.createUser();
        apiRequestUtils.postObject("/api/admin/users/add", user, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(18)
    void shouldNotAddWithIncorrectEmail() throws Exception {
        User user = UserBuilder.createUser();
        user.setEmail("mordo@gmailcom");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(19)
    void shouldNotAddWithIncorrectUsername() throws Exception {
        User user = UserBuilder.createUser();
        user.setUsername("ex");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(
                "Nazwa uÅ¼ytkownika musi posiadaÄ\u0087 od 3 do 20 znakÃ³w i nie moÅ¼e zawieraÄ\u0087 spacji",
                errors.get("username"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(20)
    void shouldNotAddWithIncorrectPassword() throws Exception {
        User user = UserBuilder.createUser();
        user.setPassword("example123");
        user.setRepeatedPassword("example123");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(
                "HasÅ\u0082o musi posiadaÄ\u0087 przynajmniej  jednÄ\u0085 duÅ¼Ä\u0085 literÄ\u0099, jednÄ\u0085 maÅ\u0082Ä\u0085 literÄ\u0099, jednÄ\u0085 cyfrÄ\u0099 i jeden znak specjalny",
                errors.get("password"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(21)
    void shouldNotAddWithExistingEmail() throws Exception {
        User user = UserBuilder.createUser();
        user.setEmail("netka@test.com");

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/add", user, status().isBadRequest());

        assertTrue((Boolean) responseParams.get("emailExists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(22)
    void shouldNotAddWithNotMatchingPasswords() throws Exception {
        User user = UserBuilder.createUser();
        user.setEmail("test21@gmail.com");
        user.setRepeatedPassword("Examplepass123");

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/add", user, status().isBadRequest());

        assertTrue((Boolean) responseParams.get("passwordsNotMatch"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(23)
    void shouldUpdateUser() throws Exception {
        User existingUser = apiRequestUtils.getObjectExpect200("/api/admin/users/show", 6, User.class);
        existingUser.setEmail("updated@email.com");

        apiRequestUtils.postAndExpect200("/api/admin/users/update", existingUser);

        User updatedUser = apiRequestUtils.getObjectExpect200("/api/admin/users/show", 6, User.class);
        assertEquals("updated@email.com", updatedUser.getEmail());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(24)
    void shouldNotAllowUnauthorizedAccessToUpdateUser() throws Exception {
        User user = new User();
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/admin/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(25)
    void shouldNotUpdateIncorrectUser() throws Exception {
        User existingUser = apiRequestUtils.getObjectExpect200("/api/admin/users/show", 6, User.class);
        existingUser.setEmail("updated@emailcom");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/update", existingUser);

        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Order(26)
    void shouldRemoveUser() throws Exception {
        User existingUser = apiRequestUtils.getObjectExpect200("/api/admin/users/show", 6, User.class);

        apiRequestUtils.postAndExpect200("/api/admin/users/remove", existingUser);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/show", 6, status().isBadRequest());
        assertNotNull(responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(27)
    void shouldNotAllowUnauthorizedAccessToRemoveUser() throws Exception {
        User user = new User();
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/admin/users/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Order(28)
    void shouldNotSelfRemove() throws Exception {
        User existingUser = apiRequestUtils.getObjectExpect200("/api/admin/users/show", 2, User.class);
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/remove", existingUser, status().isBadRequest());
        assertTrue((Boolean) responseBody.get("illegalRemoval"));
    }
}