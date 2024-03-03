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
    void shouldNotAllowUnauthorizedAccessToAllUsers() throws Exception {
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
        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/show", 5, status().isOk());
        User user = apiRequestUtils.deserializeObject(responseParams.get("user"), User.class);
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
    void shouldGetNewUserObject() throws Exception {
        Object user = apiRequestUtils.fetchObject("/api/admin/users/add", User.class);
        assertInstanceOf(User.class, user);
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(14)
    void shouldNotAllowUnauthorizedAccessToNewUserObject() throws Exception {
        mockMvc.perform(get("/api/admin/users/add"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(15)
    void shouldAddNewUser() throws Exception {
        User user = UserBuilder.createCorrectUser();
        apiRequestUtils.postObject("/api/admin/users/add", user, status().isOk());
        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/show", 6, status().isOk());
        User persistedUser = apiRequestUtils.deserializeObject(responseParams.get("user"), User.class);
        assertEquals("exampleUser", persistedUser.getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(16)
    void shouldNotAllowUnauthorizedToAddUser() throws Exception {
        User user = UserBuilder.createCorrectUser();
        apiRequestUtils.postObject("/api/admin/users/add", user, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(17)
    void shouldNotAddIncorrectUser() throws Exception {
        User user = UserBuilder.createIncorrectUser();

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/add", user, status().isBadRequest());
        Map<?, ?> errors = (Map<?, ?>) responseParams.get("errors");

        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(18)
    void shouldUpdateUser() throws Exception {
        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/show", 6, status().isOk());
        User existingUser = apiRequestUtils.deserializeObject(responseParams.get("user"), User.class);
        existingUser.setEmail("updated@email.com");

        apiRequestUtils.postObject("/api/admin/users/update", existingUser, status().isOk());

        responseParams =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/show", 6, status().isOk());
        User updatedUser = apiRequestUtils.deserializeObject(responseParams.get("user"), User.class);
        assertEquals("updated@email.com", updatedUser.getEmail());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(19)
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
    @Order(20)
    void shouldNotUpdateIncorrectUser() throws Exception {
        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/show", 6, status().isOk());
        User existingUser = apiRequestUtils.deserializeObject(responseParams.get("user"), User.class);
        existingUser.setEmail("updated@emailcom");

        Map<String, Object> responseMap =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/add", existingUser, status().isBadRequest());
        Map<?, ?> errors = (Map<?, ?>) responseMap.get("errors");

        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Order(21)
    void shouldRemoveUser() throws Exception {
        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/show", 6, status().isOk());
        User existingUser = apiRequestUtils.deserializeObject(responseParams.get("user"), User.class);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/remove", existingUser, status().isOk());
        assertTrue((Boolean) responseBody.get("success"));

        responseBody =
                apiRequestUtils.postAndReturnResponseBody("/api/admin/users/show", 6, status().isBadRequest());
        assertTrue((Boolean) responseBody.get("error"));
        assertNotNull(responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Order(22)
    void shouldNotAllowUnauthorizedAccessToRemoveUser() throws Exception {
        User user = new User();
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/admin/users/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }
}