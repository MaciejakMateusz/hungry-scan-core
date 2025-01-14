package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantService;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
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
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldGetAllUsers() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users", User.class);

        assertEquals(2, users.size());
        assertEquals("matimemek@test.com", users.getFirst().getUsername());
        assertEquals("netka@test.com", users.getLast().getEmail());
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
        assertEquals("netka@test.com", users.get(1).getUsername());
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
        assertEquals("kucharz@antek.pl", users.getFirst().getUsername());
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
        assertEquals("netka@test.com", users.getFirst().getUsername());
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
        assertEquals("admin@example.com", users.getFirst().getUsername());
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
    void shouldShowUserByUsername() throws Exception {
        User user = apiRequestUtils.postObjectExpect200("/api/admin/users/show", "restaurator@rarytas.pl", User.class);
        assertEquals("restaurator@rarytas.pl", user.getUsername());
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
                        "/api/admin/users/show", "nonExisting@email.com", status().isBadRequest());
        assertEquals("Nie znaleziono użytkownika.", responseBody.get("exceptionMsg"));
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
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Rollback
    @Transactional
    void shouldAddNewUser() throws Exception {
        User user = createUser(1L);
        RegistrationDTO registrationDTO = userMapper.toDTO(user);
        apiRequestUtils.postAndExpect200("/api/admin/users/add", registrationDTO);

        User persistedUser = findUser("example@example.com");
        assertEquals("example@example.com", persistedUser.getUsername());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void shouldNotAllowToAddUser() throws Exception {
        User user = createUser(1L);
        RegistrationDTO registrationDTO = userMapper.toDTO(user);
        apiRequestUtils.postAndExpect("/api/admin/users/add", registrationDTO, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAddUser() throws Exception {
        User user = createUser(1L);
        RegistrationDTO registrationDTO = userMapper.toDTO(user);
        apiRequestUtils.postAndExpectForbidden("/api/admin/users/add", registrationDTO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotAddWithIncorrectEmail() throws Exception {
        User user = createUser(1L);
        user.setEmail("mordo@gmailcom");
        RegistrationDTO registrationDTO = userMapper.toDTO(user);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", registrationDTO);

        assertEquals(1, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    void shouldNotAddWithIncorrectUsername() throws Exception {
        User user = createUser(2L);
        user.setUsername("ex");
        RegistrationDTO registrationDTO = userMapper.toDTO(user);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", registrationDTO);

        assertEquals(1, errors.size());
        assertEquals(
                "Niepoprawny format adresu email",
                errors.get("username"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotAddWithIncorrectPassword() throws Exception {
        User user = createUser(1L);
        user.setPassword("example123");
        user.setRepeatedPassword("example123");
        RegistrationDTO registrationDTO = userMapper.toDTO(user);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", registrationDTO);

        assertEquals(1, errors.size());
        assertEquals(
                "Hasło musi posiadać przynajmniej  jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny",
                errors.get("password"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    void shouldNotAddWithExistingUsername() throws Exception {
        User user = createUser(2L);
        user.setUsername("netka@test.com");
        user.setEmail("netka@test.com");
        RegistrationDTO registrationDTO = userMapper.toDTO(user);

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/add", registrationDTO, status().isBadRequest());

        assertEquals("Konto z podanym adresem email już istnieje", responseParams.get("username"));
        assertEquals("netka@test.com", responseParams.get("givenUsername"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotAddWithNotMatchingPasswords() throws Exception {
        User user = createUser(1L);
        user.setRepeatedPassword("Examplepass123");
        RegistrationDTO registrationDTO = userMapper.toDTO(user);

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/add", registrationDTO, status().isBadRequest());

        assertEquals("Hasła nie są identyczne", responseParams.get("repeatedPassword"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Rollback
    @Transactional
    void shouldUpdateUser() throws Exception {
        User existingUser = findUser("admin@example.com");
        existingUser.setEmail("updated@email.com");
        RegistrationDTO registrationDTO = userMapper.toDTO(existingUser);

        apiRequestUtils.patchAndExpect200("/api/admin/users/update", registrationDTO);

        User updatedUser = findUser("admin@example.com");
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
        User existingUser = apiRequestUtils.postObjectExpect200("/api/admin/users/show", "netka@test.com", User.class);
        existingUser.setEmail("wronk@email");
        RegistrationDTO registrationDTO = userMapper.toDTO(existingUser);

        Map<?, ?> errors = apiRequestUtils.patchAndExpectErrors("/api/admin/users/update", registrationDTO);

        assertEquals(1, errors.size());
        assertEquals("Niepoprawny format adresu email", errors.get("email"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Rollback
    @Transactional
    void shouldRemoveUser() throws Exception {
        User existingUser = findUser("netka@test.com");
        assertNotNull(existingUser);

        apiRequestUtils.deleteAndExpect200("/api/admin/users/delete", "netka@test.com");

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/show", "netka@test.com", status().isBadRequest());
        assertEquals("Nie znaleziono użytkownika.", responseBody.get("exceptionMsg"));
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
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldNotSelfRemove() throws Exception {
        Map<String, Object> responseBody =
                apiRequestUtils.deleteAndReturnResponseBody(
                        "/api/admin/users/delete", "admin@example.com", status().isBadRequest());
        assertTrue((Boolean) responseBody.get("illegalRemoval"));
    }

    private User createUser(Long restaurantId) {
        User user = new User();
        user.setForename("Name");
        user.setSurname("Surname");
        user.setUsername("example@example.com");
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
        RestaurantDTO restaurantDTO = null;
        try {
            restaurantDTO = restaurantService.findById(restaurantId);
        } catch (LocalizedException e) {
            log.error(e.getMessage());
        }
        assert Objects.nonNull(restaurantDTO);
        return restaurantMapper.toRestaurant(restaurantDTO);
    }

    private User findUser(String username) throws Exception {
        return apiRequestUtils.postObjectExpect200("/api/admin/users/show", username, User.class);
    }
}