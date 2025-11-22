package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import com.hackybear.hungry_scan_core.test_utils.ApiJwtRequestUtils;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

import static com.hackybear.hungry_scan_core.utility.Fields.*;
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
class UserControllerTest {

    @Autowired
    private ApiJwtRequestUtils apiRequestUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private CacheManager cacheManager;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {STAFF})
    void shouldGetUserProfileData() throws Exception {
        UserProfileDTO userProfileDTO = apiRequestUtils.fetchObject("/api/user/profile", UserProfileDTO.class);
        assertNotNull(userProfileDTO);
        assertEquals("admin@example.com", userProfileDTO.username());
        assertEquals("edmin", userProfileDTO.forename());
        assertEquals("edminowsky", userProfileDTO.surname());
    }

    @Test
    void shouldNotGetUserProfileData_expectForbidden() throws Exception {
        apiRequestUtils.executeGet("/api/user/profile", status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {STAFF})
    void shouldNotUpdateUserProfile_wrongPassword() throws Exception {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(
                "Admin",
                "Super",
                "WrongPass923.",
                "Karagor500?",
                "Karagor500?"
        );

        Map<?, ?> response = apiRequestUtils.patchAndReturnResponseBody(
                "/api/user/profile", dto, status().isUnauthorized());
        assertEquals("Błędne hasło", response.get("password"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {STAFF})
    void shouldNotUpdateUserProfile_newPasswordsEmpty() throws Exception {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(
                "Admin",
                "Super",
                "TestPass923.",
                null,
                null
        );

        Map<?, ?> response = apiRequestUtils.patchAndReturnResponseBody(
                "/api/user/profile", dto, status().isBadRequest());
        assertEquals("Hasło nie powinno być puste", response.get("newPassword"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {STAFF})
    void shouldNotUpdateUserProfile_wrongPasswordFormat() throws Exception {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(
                "Admin",
                "Super",
                "TestPass923.",
                "Karagor500",
                "Karagor500"
        );

        Map<?, ?> response = apiRequestUtils.patchAndReturnResponseBody(
                "/api/user/profile", dto, status().isBadRequest());
        assertEquals("Hasło musi posiadać przynajmniej  jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny",
                response.get("newPassword"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {ADMIN})
    void shouldNotUpdateUserProfile_passwordsNotTheSame() throws Exception {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(
                "Admin",
                "Super",
                "TestPass923.",
                "Karagor500!",
                "Karagor600!"
        );

        Map<?, ?> response = apiRequestUtils.patchAndReturnResponseBody(
                "/api/user/profile", dto, status().isBadRequest());
        assertEquals("Hasła nie są identyczne", response.get("repeatedPassword"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {MANAGER})
    @Transactional
    @Rollback
    void shouldUpdateUserProfile_onlyBasicData() throws Exception {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(
                "Admin",
                "Super",
                null,
                null,
                null
        );

        apiRequestUtils.patchAndExpect200("/api/user/profile", dto);
        UserProfileDTO userProfileDTO = apiRequestUtils.fetchObject("/api/user/profile", UserProfileDTO.class);
        User user = userMapper.toUser(userProfileDTO);
        user.setUsername("shouldNotApply@example.com");
        user.setForename("Admin");
        user.setSurname("Super");
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {STAFF})
    @Transactional
    @Rollback
    void shouldUpdateUserProfile_withPasswords() throws Exception {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(
                "Admin",
                "Super",
                "TestPass923.",
                "Karagor500?",
                "Karagor500?"
        );

        apiRequestUtils.patchAndExpect200("/api/user/profile", dto);
        UserProfileDTO userProfileDTO = apiRequestUtils.fetchObject("/api/user/profile", UserProfileDTO.class);
        User user = userMapper.toUser(userProfileDTO);
        user.setUsername("shouldNotApply@example.com");
        user.setForename("Admin");
        user.setSurname("Super");

        AuthRequestDTO authRequestDTO = new AuthRequestDTO("admin@example.com", "Karagor500?");
        Map<?, ?> response =
                apiRequestUtils.postAndFetchObject("/api/user/login", authRequestDTO, Map.class);
        assertEquals("/app", response.get("redirectUrl"));
        assertEquals("Admin", response.get("forename"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldRegister() throws Exception {
        RegistrationDTO registrationDTO = createRegistrationDTO();
        apiRequestUtils.postAndExpect200("/api/user/register", registrationDTO);
        User persistedUser = getDetachedUser("juan.bomboclat@test.com");
        assertNotNull(persistedUser);
        assertEquals("Juan", persistedUser.getForename());
        assertEquals(4, persistedUser.getOrganizationId());
        assertNotNull(persistedUser.getEmailToken());
        assertEquals(0, persistedUser.getEnabled());
    }

    @Test
    @Transactional
    @Rollback
    void shouldNotRegisterWithIncorrectFields() throws Exception {
        RegistrationDTO registrationDTO = createIncorrectRegistrationDTO();
        Map<?, ?> response = apiRequestUtils.postAndExpectErrors("/api/user/register", registrationDTO);
        assertEquals(3, response.size());
        assertEquals(response.get("password"), "Hasło musi posiadać przynajmniej  jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny");
        assertEquals(response.get("forename"), "Pole nie może być puste");
        assertEquals(response.get("username"), "Niepoprawny format adresu email");
    }

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateAndLoginUser() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("matimemek@test.com", "Lubieplacki123!");
        Map<?, ?> response =
                apiRequestUtils.postAndFetchObject("/api/user/login", authRequestDTO, Map.class);
        assertEquals("/app", response.get("redirectUrl"));
        assertEquals("mati", response.get("forename"));
    }

    @Test
    void shouldLoginAndReturnUnauthorized() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("iDoNotExist", "DoesNotMatter123!");
        apiRequestUtils.postAndExpectForbidden("/api/user/login", authRequestDTO);
    }

    @Test
    @Transactional
    @Rollback
    void shouldNotLoginNotActive() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("netka@test.com", "password");
        Map<?, ?> response =
                apiRequestUtils.postAndReturnResponseBody("/api/user/login", authRequestDTO, status().isForbidden());
        assertEquals(response.size(), 1);
        assertEquals(response.get("message"), "notActivated");
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void shouldSwitchRestaurant() throws Exception {
        User currentUser = getDetachedUser("admin@example.com");
        assertEquals(1, currentUser.getActiveRestaurantId());
        assertEquals(1, currentUser.getActiveMenuId());
        apiRequestUtils.patchAndExpect200("/api/user/restaurant", 2);

        currentUser = getDetachedUser("admin@example.com");
        assertEquals(2, currentUser.getActiveRestaurantId());
        assertEquals(2, currentUser.getActiveMenuId());
    }

    @Test
    void shouldNotAllowUnauthorizedToSwitchRestaurant() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/user/restaurant", 2);
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void shouldGetCurrentRestaurant() throws Exception {
        RestaurantDTO restaurant =
                apiRequestUtils.fetchObject("/api/user/current-restaurant", RestaurantDTO.class);
        assertNotNull(restaurant);
        assertEquals(1L, restaurant.id());
        assertEquals("Rarytas", restaurant.name());
        assertEquals("ul. Główna 123, Miastowo, Województwo, 54321", restaurant.address());
        assertEquals(1, restaurant.menus().size());
    }

    @Test
    void shouldNotAllowUnauthorizedToGetCurrentRestaurant() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/user/current-restaurant");
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"MANAGER"}, username = "netka@test.com")
    void shouldSwitchMenu() throws Exception {
        User currentUser = getDetachedUser("netka@test.com");
        assertEquals(1, currentUser.getActiveMenuId());
        apiRequestUtils.patchAndExpect200("/api/user/menu", 2);

        currentUser = getDetachedUser("netka@test.com");
        assertEquals(2, currentUser.getActiveMenuId());
    }

    @Test
    void shouldNotAllowUnauthorizedToSwitchMenu() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/user/menu", 2);
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = {"ADMIN"}, username = "admin@example.com")
    void shouldGetCurrentMenu() throws Exception {
        Objects.requireNonNull(cacheManager.getCache("USER_MENU")).clear();
        MenuDTO menu = apiRequestUtils.fetchObject("/api/user/current-menu", MenuDTO.class);
        assertNotNull(menu);
        assertEquals(1L, menu.id());
        assertEquals("Całodniowe", menu.name());
        assertEquals(9, menu.categories().size());
        assertTrue(menu.standard());
    }

    @Test
    void shouldNotAllowUnauthorizedToGetCurrentMenu() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/user/current-menu");
    }

    private User getDetachedUser(String username) {
        User user = userRepository.findUserByUsername(username);
        entityManager.detach(user);
        return user;
    }

    private RegistrationDTO createRegistrationDTO() {
        User user = new User();
        user.setForename("Juan");
        user.setSurname("Bomboclat");
        user.setUsername("juan.bomboclat@test.com");
        user.setPassword("Password123!");
        user.setRepeatedPassword("Password123!");
        return userMapper.toRegistrationDTO(user);
    }

    private RegistrationDTO createIncorrectRegistrationDTO() {
        User user = new User();
        user.setForename("");
        user.setSurname("Bomboclat");
        user.setUsername("juan.bomboclat@testcom");
        user.setPassword("Password123");
        user.setRepeatedPassword("Password123!");
        return userMapper.toRegistrationDTO(user);
    }

}