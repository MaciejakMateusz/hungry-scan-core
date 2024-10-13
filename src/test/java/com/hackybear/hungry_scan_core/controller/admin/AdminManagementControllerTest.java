package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminManagementControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    void shouldGetAllUsers() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users", User.class);

        assertEquals(5, users.size());
        assertEquals("mati", users.get(0).getUsername());
        assertEquals("2c73bfc-16fc@temp.it", users.get(4).getEmail());
        assertTrue(users.stream().allMatch(user -> user.getOrganizationId() == 1L));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowAccessToUsers() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUsers() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetWaiters() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users/waiters", User.class);

        assertEquals(2, users.size());
        assertEquals("neta", users.get(1).getUsername());
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowAccessToWaiters() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/waiters");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToWaiters() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/waiters");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetCooks() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users/cooks", User.class);

        assertEquals(1, users.size());
        assertEquals("kucharz", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowAccessToCooks() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/cooks");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToCooks() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/cooks");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetManagers() throws Exception {
        List<User> users = apiRequestUtils.fetchAsList(
                "/api/admin/users/managers", User.class);

        assertEquals(1, users.size());
        assertEquals("neta", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowAccessToManagers() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/managers");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToManagers() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/managers");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAdmins() throws Exception {
        List<User> users = apiRequestUtils.fetchAsList(
                "/api/admin/users/admins", User.class);

        assertEquals(2, users.size());
        assertEquals("admin", users.get(0).getUsername());
    }

    @Test
    @WithMockUser(roles = "COOK")
    void shouldNotAllowAccessToAdmins() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/admins");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAdmins() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/admins");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowUserById() throws Exception {
        User user = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 7, User.class);
        assertEquals("owner", user.getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowAccessToShowUser() throws Exception {
        apiRequestUtils.postAndExpect("/api/admin/users/show", 4, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToShowUser() throws Exception {
        apiRequestUtils.postAndExpectForbidden("/api/admin/users/show", 4);
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
    void shouldNotAllowAccessToNewUserObject() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/add");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToNewUserObject() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/add");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    @Rollback
    @Transactional
    void shouldAddNewUser() throws Exception {
        User user = createUser(1L, 1L);
        apiRequestUtils.postAndExpect200("/api/admin/users/add", user);

        User persistedUser = apiRequestUtils.postObjectExpect200("/api/admin/users/show", 9, User.class);
        assertEquals("exampleUser", persistedUser.getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowToAddUser() throws Exception {
        User user = createUser(1L, 1L);
        apiRequestUtils.postAndExpect("/api/admin/users/add", user, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAddUser() throws Exception {
        User user = createUser(1L, 1L);
        apiRequestUtils.postAndExpectForbidden("/api/admin/users/add", user);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectEmail() throws Exception {
        User user = createUser(1L, 1L);
        user.setEmail("mordo@gmailcom");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertEquals(1, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "owner")
    void shouldNotAddWithIncorrectUsername() throws Exception {
        User user = createUser(2L, 2L);
        user.setUsername("ex");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertEquals(1, errors.size());
        assertEquals(
                "Nazwa użytkownika musi posiadać od 3 do 20 znaków i nie może zawierać spacji",
                errors.get("username"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    void shouldNotAddWithIncorrectPassword() throws Exception {
        User user = createUser(1L, 1L);
        user.setPassword("example123");
        user.setRepeatedPassword("example123");

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", user);

        assertEquals(1, errors.size());
        assertEquals(
                "Hasło musi posiadać przynajmniej  jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny",
                errors.get("password"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "owner")
    void shouldNotAddWithExistingEmail() throws Exception {
        User user = createUser(2L, 2L);
        user.setUsername("mleczyk");
        user.setEmail("netka@test.com");

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/add", user, status().isBadRequest());

        assertTrue((Boolean) responseParams.get("emailExists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    void shouldNotAddWithNotMatchingPasswords() throws Exception {
        User user = createUser(1L, 1L);
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
    void shouldNotAllowAccessToUpdateUser() throws Exception {
        apiRequestUtils.patchAndExpect("/api/admin/users/update", new User(), status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUpdateUser() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/admin/users/update", new User());
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
    void shouldNotAllowAccessToRemoveUser() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/admin/users/delete", new User(), status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToRemoveUser() throws Exception {
        apiRequestUtils.deleteAndExpect("/api/admin/users/delete", new User(), status().isForbidden());
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

    private User createUser(Long restaurantId, Long organizationId) {
        User user = new User();
        user.setOrganizationId(organizationId);
        user.setEmail("example@example.com");
        user.setUsername("exampleUser");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        user.setRoles(new HashSet<>(Collections.singletonList(createRole())));
        user.addRestaurant(getRestaurant(restaurantId));
        return user;
    }

    private Role createRole() {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_WAITER");
        role.setDisplayedName(getDefaultTranslation());
        return role;
    }

    private Translatable getDefaultTranslation() {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation("Kelner");
        return translatable;
    }

    private Restaurant getRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId).orElse(new Restaurant());
    }
}