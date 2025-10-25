package com.hackybear.hungry_scan_core.controller.admin;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.UserActivityDTO;
import com.hackybear.hungry_scan_core.dto.UserDTO;
import com.hackybear.hungry_scan_core.dto.UserProfileDTO;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.hackybear.hungry_scan_core.utility.Fields.STAFF;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
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
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private EmailService emailService;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldGetAllUsers() throws Exception {
        List<UserDTO> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users", UserDTO.class);

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.username().equals("matimemek@test.com")));
        assertTrue(users.stream().anyMatch(user -> user.username().equals("netka@test.com")));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldNotAllowAccessToUsers() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUsers() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldGetUsersActivity() throws Exception {
        List<UserActivityDTO> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users/activity", UserActivityDTO.class);

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.username().equals("matimemek@test.com")));
        assertTrue(users.stream().anyMatch(user -> user.username().equals("netka@test.com")));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldNotAllowAccessUserActivity() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/activity");
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUsersActivity() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/admin/users/activity");
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    void shouldGetAllRoles() throws Exception {
        List<Role> roles =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users/roles", Role.class);

        assertEquals(5, roles.size());
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_MANAGER")));
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_CUSTOMER_READONLY")));
        assertTrue(roles.stream().anyMatch(role -> role.getName().equals("ROLE_STAFF")));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Rollback
    @Transactional
    void shouldAddNewUser() throws Exception {
        User user = createUser(1L);
        UserDTO userDTO = userMapper.toDTO(user);
        apiRequestUtils.postAndExpect200("/api/admin/users/add", userDTO);

        UserProfileDTO persistedUser = findUser("example@example.com");
        assertEquals("example@example.com", persistedUser.username());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    @Rollback
    @Transactional
    void shouldNotAllowToAddUser() throws Exception {
        User user = createUser(1L);
        RegistrationDTO registrationDTO = userMapper.toRegistrationDTO(user);
        apiRequestUtils.postAndExpect("/api/admin/users/add", registrationDTO, status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToAddUser() throws Exception {
        User user = createUser(1L);
        RegistrationDTO registrationDTO = userMapper.toRegistrationDTO(user);
        apiRequestUtils.postAndExpectForbidden("/api/admin/users/add", registrationDTO);
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    @Rollback
    @Transactional
    void shouldNotAddWithIncorrectUsername() throws Exception {
        User user = createUser(2L);
        user.setUsername("ex");
        UserDTO userDTO = userMapper.toDTO(user);

        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/admin/users/add", userDTO);

        assertEquals(1, errors.size());
        assertEquals(
                "Niepoprawny format adresu email",
                errors.get("username"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "restaurator@rarytas.pl")
    @Rollback
    @Transactional
    void shouldNotAddWithExistingUsername() throws Exception {
        User user = createUser(2L);
        user.setUsername("netka@test.com");
        user.setEmail("netka@test.com");
        UserDTO userDTO = userMapper.toDTO(user);

        Map<String, Object> responseParams =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/add", userDTO, status().isBadRequest());

        assertEquals("Konto z podanym adresem email już istnieje", responseParams.get("username"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Rollback
    @Transactional
    void shouldUpdateUser() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users", User.class);
        User user = users.stream()
                .filter(u -> u.getUsername().equals("matimemek@test.com"))
                .findFirst()
                .orElseThrow();
        assertEquals("mati", user.getForename());
        assertTrue(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_STAFF")));
        assertEquals(1, user.getRestaurants().size());
        assertTrue(user.getRestaurants().stream().anyMatch(restaurant -> restaurant.getId().equals(1L)));

        user.setForename("Gregor");

        List<Role> roles =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users/roles", Role.class);
        Role role = roles.stream().filter(r -> r.getName().equals("ROLE_MANAGER")).findFirst().orElseThrow();
        user.getRoles().clear();
        user.setRoles(Set.of(role));

        user.getRestaurants().clear();
        Restaurant restaurant = getRestaurant(4L);
        user.addRestaurant(restaurant);

        UserDTO userDTO = userMapper.toDTO(user);

        apiRequestUtils.patchAndExpect200("/api/admin/users/update", userDTO);

        User updatedUser = userRepository.findUserByUsername("matimemek@test.com");
        assertEquals("Gregor", updatedUser.getForename());
        assertTrue(updatedUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_MANAGER")));
        assertEquals(1, updatedUser.getRestaurants().size());
        assertTrue(updatedUser.getRestaurants().stream().anyMatch(r -> r.getId().equals(4L)));
        assertEquals(4L, updatedUser.getActiveRestaurantId());
        assertEquals(5L, updatedUser.getActiveMenuId());
    }

    @Test
    @WithMockUser(roles = "MANAGER", username = "netka@test.com")
    @Rollback
    @Transactional
    void shouldNotAllowAdminElevation() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users", User.class);
        User user = users.stream()
                .filter(u -> u.getUsername().equals("matimemek@test.com"))
                .findFirst()
                .orElseThrow();
        assertEquals("mati", user.getForename());
        assertTrue(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_STAFF")));
        assertEquals(1, user.getRestaurants().size());
        assertTrue(user.getRestaurants().stream().anyMatch(restaurant -> restaurant.getId().equals(1L)));

        user.setForename("Gregor");

        List<Role> roles =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users/roles", Role.class);
        Role role = roles.stream().filter(r -> r.getName().equals("ROLE_ADMIN")).findFirst().orElseThrow();
        user.getRoles().clear();
        user.setRoles(Set.of(role));

        UserDTO userDTO = userMapper.toDTO(user);

        Map<?, ?> response = apiRequestUtils.patchAndReturnResponseBody(
                "/api/admin/users/update", userDTO, status().isForbidden());
        assertEquals("Nie masz uprawnień do wykonania tej akcji.", response.get("message"));
    }

    @Test
    @WithMockUser(roles = "MANAGER", username = "netka@test.com")
    @Rollback
    @Transactional
    void shouldNotUpdateIncorrectUser() throws Exception {
        List<User> users =
                apiRequestUtils.fetchAsList(
                        "/api/admin/users", User.class);
        User user = users.stream()
                .filter(u -> u.getUsername().equals("matimemek@test.com"))
                .findFirst()
                .orElseThrow();
        assertEquals("mati", user.getForename());
        assertTrue(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_STAFF")));
        assertEquals(1, user.getRestaurants().size());
        assertTrue(user.getRestaurants().stream().anyMatch(restaurant -> restaurant.getId().equals(1L)));

        user.setForename("");
        user.setSurname("Jones1");
        user.getRestaurants().clear();
        user.getRoles().clear();

        UserDTO userDTO = userMapper.toDTO(user);

        Map<?, ?> response = apiRequestUtils.patchAndReturnResponseBody(
                "/api/admin/users/update", userDTO, status().isBadRequest());
        assertEquals("Pole nie może być puste", response.get("forename"));
        assertEquals("Pole powinno mieć minimum dwa znaki i zawierać tylko litery", response.get("surname"));
        assertEquals("Pole nie może być puste", response.get("roles"));
        assertEquals("Pole nie może być puste", response.get("restaurants"));
    }

    @Test
    @WithMockUser(roles = STAFF)
    void shouldNotAllowAccessToUpdateUser() throws Exception {
        apiRequestUtils.patchAndExpect("/api/admin/users/update", new User(), status().isForbidden());
    }

    @Test
    void shouldNotAllowUnauthorizedAccessToUpdateUser() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/admin/users/update", new User());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin@example.com")
    @Rollback
    @Transactional
    void shouldRemoveUser() throws Exception {
        UserProfileDTO existingUser = findUser("netka@test.com");
        assertNotNull(existingUser);

        apiRequestUtils.deleteAndExpect200("/api/admin/users/delete", "netka@test.com");

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/admin/users/profile", "netka@test.com", status().isBadRequest());
        assertEquals("Nie znaleziono użytkownika.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
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
        role.setName("ROLE_STAFF");
        role.setDisplayedName(getDefaultTranslation());
        return role;
    }

    private Translatable getDefaultTranslation() {
        Translatable translatable = new Translatable();
        translatable.setPl("Personel");
        translatable.setEn("Staff");
        return translatable;
    }

    private Restaurant getRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId).orElseThrow();
    }

    private UserProfileDTO findUser(String username) throws Exception {
        return apiRequestUtils.postAndFetchObject("/api/admin/users/profile", username, UserProfileDTO.class);
    }

}