package com.hackybear.hungry_scan_core.controller.login;

import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.EmailService;
import com.hackybear.hungry_scan_core.test_utils.ApiJwtRequestUtils;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
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

    @MockBean
    private EmailService emailService;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @Transactional
    @Rollback
    void shouldRegister() throws Exception {
        RegistrationDTO registrationDTO = createRegistrationDTO();
        apiRequestUtils.postAndExpect200("/api/user/register", registrationDTO);
        User persistedUser = getDetachedUser("juan.bomboclat@test.com");
        assertNotNull(persistedUser);
        assertEquals("Juan", persistedUser.getName());
        assertEquals(3, persistedUser.getOrganizationId());
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
        assertEquals(response.get("name"), "Pole nie może być puste");
        assertEquals(response.get("username"), "Niepoprawny format adresu email");
    }

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateAndLoginUser() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("matimemek@test.com", "Lubieplacki123!");
        Map<?, ?> response =
                apiRequestUtils.postAndFetchObject("/api/user/login", authRequestDTO, Map.class);
        assertEquals("authorized", response.get("message"));
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
        apiRequestUtils.patchAndExpect200("/api/user/restaurant", 2);

        currentUser = getDetachedUser("admin@example.com");
        assertEquals(2, currentUser.getActiveRestaurantId());
    }

    @Test
    void shouldNotAllowUnauthorizedToSwitchRestaurant() throws Exception {
        apiRequestUtils.patchAndExpectForbidden("/api/user/restaurant", 2);
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
    void shouldScan() throws Exception {
        String existingRestaurantToken = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed";
        MockHttpServletResponse response =
                apiRequestUtils.getResponse("/api/user/scan/" + existingRestaurantToken);
        assertEquals(302, response.getStatus());

        Optional<Cookie> jwtCookie = Arrays.stream(response.getCookies()).findFirst();
        assert jwtCookie.isPresent();
        assertEquals("jwt", jwtCookie.get().getName());

        List<String> headers = response.getHeaders("Location");
        assertEquals(1, headers.size());
        assertEquals("http://localhost:3001", headers.get(0));
    }

    @Test
    @Transactional
    @Rollback
    void shouldScanAndExpect400() throws Exception {
        String nonExistingRestaurantToken = "3d12381d-21d2-55f8-80b3-d666d5f0a8ed";
        Map<?, ?> errors = apiRequestUtils.getAndExpectErrors("/api/user/scan/" + nonExistingRestaurantToken);
        assertEquals(1, errors.size());
        assertEquals("Restauracja z podanym tokenem nie istnieje.", errors.get("exceptionMsg"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldScanAndExpectNewCustomerDetails() throws Exception {
        List<User> currentRestaurantUsers = userRepository.findAllByActiveRestaurantId(1L);
        assertEquals(5, currentRestaurantUsers.size());
        String existingRestaurantToken = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed";

        apiRequestUtils.getResponse("/api/user/scan/" + existingRestaurantToken);

        List<User> updatedRestaurantUsers = userRepository.findAllByActiveRestaurantId(1L);
        assertEquals(6, updatedRestaurantUsers.size());
        User newTempUser = updatedRestaurantUsers.get(updatedRestaurantUsers.size() - 1);
        assertEquals(1L, newTempUser.getActiveRestaurantId());
        assertEquals(0L, newTempUser.getOrganizationId());
        assertNotNull(newTempUser.getJwtToken());

        Set<Role> roles = newTempUser.getRoles();
        assertEquals(1, roles.size());
        assertEquals("ROLE_CUSTOMER_READONLY", roles.iterator().next().getName());
    }

    private User getDetachedUser(String username) {
        User user = userRepository.findUserByUsername(username);
        entityManager.detach(user);
        return user;
    }

    private RegistrationDTO createRegistrationDTO() {
        User user = new User();
        user.setName("Juan");
        user.setSurname("Bomboclat");
        user.setUsername("juan.bomboclat@test.com");
        user.setPassword("Password123!");
        user.setRepeatedPassword("Password123!");
        return userMapper.toDTO(user);
    }

    private RegistrationDTO createIncorrectRegistrationDTO() {
        User user = new User();
        user.setName("");
        user.setSurname("Bomboclat");
        user.setUsername("juan.bomboclat@testcom");
        user.setPassword("Password123");
        user.setRepeatedPassword("Password123!");
        return userMapper.toDTO(user);
    }

}