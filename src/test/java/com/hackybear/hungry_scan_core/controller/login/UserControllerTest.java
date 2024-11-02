package com.hackybear.hungry_scan_core.controller.login;

import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiJwtRequestUtils;
import jakarta.persistence.EntityManager;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    }

    @Test
    @Transactional
    @Rollback
    void shouldNotRegisterWithIncorrectFields() throws Exception {
        RegistrationDTO registrationDTO = createRegistrationDTO();
        apiRequestUtils.postAndExpect200("/api/user/register", registrationDTO);
        User persistedUser = getDetachedUser("juan.bomboclat@test.com");
        assertNotNull(persistedUser);
        assertEquals("Juan", persistedUser.getName());
        assertEquals(3, persistedUser.getOrganizationId());
    }

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateAndLoginUser() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("matimemek@test.com", "Lubieplacki123!");
        Map<String, Object> response =
                apiRequestUtils.postAndFetchObject("/api/user/login", authRequestDTO, Map.class);
        assertEquals("Login successful", response.get("message"));
    }

    @Test
    void shouldLoginAndReturnUnauthorized() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("iDoNotExist", "DoesNotMatter123!");
        apiRequestUtils.postAndExpectForbidden("/api/user/login", authRequestDTO);
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

}