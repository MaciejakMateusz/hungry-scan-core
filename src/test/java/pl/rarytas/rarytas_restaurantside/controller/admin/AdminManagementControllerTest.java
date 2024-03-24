package pl.rarytas.rarytas_restaurantside.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Role;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AdminManagementControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldGetAllUsers() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users", User.class);

        assertEquals(5, users.size());
        assertEquals("mati", users.get(0).getUsername());
        assertEquals("kucharz@antek.pl", users.get(3).getEmail());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldGetWaiters() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users/waiters", User.class);

        assertEquals(2, users.size());
        assertEquals("neta", users.get(1).getUsername());
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToWaiters() throws Exception {
        mockMvc.perform(get("/api/admin/users/waiters"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldGetCooks() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users/cooks", User.class);

        assertEquals(1, users.size());
        assertEquals("kucharz", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToCooks() throws Exception {
        mockMvc.perform(get("/api/admin/users/waiters"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldGetManagers() throws Exception {
        List<User> users = apiRequestUtils.fetchAsList(
                "/api/admin/users/managers", User.class);

        assertEquals(2, users.size());
        assertEquals("neta", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToManagers() throws Exception {
        mockMvc.perform(get("/api/admin/users/managers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldGetAdmins() throws Exception {
        List<User> users = apiRequestUtils.fetchAsList(
                "/api/admin/users/admins", User.class);

        assertEquals(2, users.size());
        assertEquals("admin", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToAdmins() throws Exception {
        mockMvc.perform(get("/api/admin/users/admins"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowUserById() throws Exception {
        User user = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 5, User.class);
        assertEquals("owner", user.getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
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
    void shouldNotShowUserById() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/show", 55, status().isBadRequest());
        assertEquals("Użytkownik z podanym ID = 55 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetNewUserObject() throws Exception {
        Object user = apiRequestUtils.fetchObject("/api/admin/users/add", User.class);
        assertInstanceOf(User.class, user);
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToNewUserObject() throws Exception {
        mockMvc.perform(get("/api/admin/users/add"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Rollback
    @Transactional
    void shouldAddNewUser() throws Exception {
        User user = createUser();
        apiRequestUtils.postAndExpect200("/api/admin/users/add", user);

        User persistedUser = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 6, User.class);
        assertEquals("exampleUser", persistedUser.getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedToAddUser() throws Exception {
        User user = createUser();
        apiRequestUtils.postAndExpect("/api/admin/users/add", user, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectEmail() throws Exception {
        User user = createUser();
        user.setEmail("mordo@gmailcom");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertEquals(1, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectUsername() throws Exception {
        User user = createUser();
        user.setUsername("ex");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertEquals(1, errors.size());
        assertEquals(
                "Nazwa użytkownika musi posiadać od 3 do 20 znaków i nie może zawierać spacji",
                errors.get("username"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectPassword() throws Exception {
        User user = createUser();
        user.setPassword("example123");
        user.setRepeatedPassword("example123");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertEquals(1, errors.size());
        assertEquals(
                "Hasło musi posiadać przynajmniej  jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny",
                errors.get("password"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithExistingEmail() throws Exception {
        User user = createUser();
        user.setUsername("mleczyk");
        user.setEmail("netka@test.com");

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/add", user, status().isBadRequest());

        assertTrue((Boolean) responseParams.get("emailExists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithNotMatchingPasswords() throws Exception {
        User user = createUser();
        user.setUsername("ExampleUser2");
        user.setEmail("test21@gmail.com");
        user.setRepeatedPassword("Examplepass123");

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/add", user, status().isBadRequest());

        assertTrue((Boolean) responseParams.get("passwordsNotMatch"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Rollback
    @Transactional
    void shouldUpdateUser() throws Exception {
        User existingUser = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 3, User.class);
        existingUser.setEmail("updated@email.com");

        apiRequestUtils.patchAndExpect200("/api/admin/users/update", existingUser);

        User updatedUser = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 3, User.class);
        assertEquals("updated@email.com", updatedUser.getEmail());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowUnauthorizedAccessToUpdateUser() throws Exception {
        User user = new User();
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(patch("/api/admin/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotUpdateIncorrectUser() throws Exception {
        User existingUser = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 3, User.class);
        existingUser.setEmail("wronk@email");

        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/admin/users/update", existingUser);

        assertEquals(1, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Rollback
    @Transactional
    void shouldRemoveUser() throws Exception {
        User existingUser = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 4, User.class);
        assertNotNull(existingUser);

        apiRequestUtils.deleteAndExpect200("/api/admin/users/delete", existingUser);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/show", 4, status().isBadRequest());
        assertEquals("Użytkownik z podanym ID = 4 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowUnauthorizedAccessToRemoveUser() throws Exception {
        User user = new User();
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(delete("/api/admin/users/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    void shouldNotSelfRemove() throws Exception {
        User existingUser = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 2, User.class);
        Map<String, Object> responseBody =
                apiRequestUtils.deleteAndReturnResponseBody(
                        "/api/admin/users/delete", existingUser, status().isBadRequest());
        assertTrue((Boolean) responseBody.get("illegalRemoval"));
    }

    private User createUser() {
        User user = new User();
        user.setEmail("example@example.com");
        user.setUsername("exampleUser");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        user.setRoles(new HashSet<>(Collections.singletonList(createRole())));
        return user;
    }

    private Role createRole() {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_WAITER");
        role.setDisplayedName("Kelner");
        return role;
    }
}