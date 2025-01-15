package com.hackybear.hungry_scan_core.integration;

import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

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
public class RegistrationFlowTest {


    @Autowired
    private ApiJwtRequestUtils apiRequestUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserMapper userMapper;

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
    void completeRegistrationFlow() throws Exception {
        loginUnregistered();
        shouldNotRegisterWithErrors();
        initialRegistration();
        loginRegisteredNotActivated();
        activateAccount();
        loginWithSuccess();
        shouldNotRegisterSecondTime();
    }

    private void loginUnregistered() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("juan.bomboclat@test.com", "Password123!");
        apiRequestUtils.postAndExpectForbidden("/api/user/login", authRequestDTO);
    }

    private void shouldNotRegisterWithErrors() throws Exception {
        RegistrationDTO registrationDTO = createIncorrectRegistrationDTO();
        Map<?, ?> response = apiRequestUtils.postAndExpectErrors("/api/user/register", registrationDTO);
        assertEquals(3, response.size());
        assertEquals(response.get("password"), "Hasło musi posiadać przynajmniej  jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny");
        assertEquals(response.get("forename"), "Pole nie może być puste");
        assertEquals(response.get("username"), "Niepoprawny format adresu email");
    }

    private void initialRegistration() throws Exception {
        RegistrationDTO registrationDTO = createRegistrationDTO();
        apiRequestUtils.postAndExpect200("/api/user/register", registrationDTO);
        User persistedUser = getDetachedUser();
        assertNotNull(persistedUser);
        assertEquals("Juan", persistedUser.getForename());
        assertEquals(4, persistedUser.getOrganizationId());
        assertNotNull(persistedUser.getEmailToken());
        assertEquals(0, persistedUser.getEnabled());
    }

    private void loginRegisteredNotActivated() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("juan.bomboclat@test.com", "Password123!");
        Map<?, ?> response =
                apiRequestUtils.postAndReturnResponseBody("/api/user/login", authRequestDTO, status().isForbidden());
        assertEquals(response.size(), 1);
        assertEquals(response.get("message"), "notActivated");
    }

    private void activateAccount() throws Exception {
        User persistedUser = getDetachedUser();
        apiRequestUtils.getResponse("/api/user/register/" + persistedUser.getEmailToken());
        persistedUser = getDetachedUser();
        assertNotNull(persistedUser);
        assertEquals("Juan", persistedUser.getForename());
        assertEquals(4, persistedUser.getOrganizationId());
        assertEquals(1, persistedUser.getEnabled());
        assertNull(persistedUser.getEmailToken());
    }

    private void loginWithSuccess() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("juan.bomboclat@test.com", "Password123!");
        Map<?, ?> response =
                apiRequestUtils.postAndFetchObject("/api/user/login", authRequestDTO, Map.class);
        assertEquals("/create-restaurant", response.get("redirectUrl"));
    }

    private void shouldNotRegisterSecondTime() throws Exception {
        RegistrationDTO registrationDTO = createRegistrationDTO();
        Map<?, ?> response =
                apiRequestUtils.postAndExpectErrors("/api/user/register", registrationDTO);
        assertEquals(2, response.size());
        assertEquals("Konto z podanym adresem email już istnieje", response.get("username"));
        assertEquals("juan.bomboclat@test.com", response.get("givenUsername"));
    }

    private User getDetachedUser() {
        User user = userRepository.findUserByUsername("juan.bomboclat@test.com");
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
        return userMapper.toDTO(user);
    }

    private RegistrationDTO createIncorrectRegistrationDTO() {
        User user = new User();
        user.setForename("");
        user.setSurname("Bomboclat");
        user.setUsername("juan.bomboclat@testcom");
        user.setPassword("Password123");
        user.setRepeatedPassword("Password123!");
        return userMapper.toDTO(user);
    }
}
