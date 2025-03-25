package com.hackybear.hungry_scan_core.integration;

import com.hackybear.hungry_scan_core.dto.AuthRequestDTO;
import com.hackybear.hungry_scan_core.dto.RecoveryDTO;
import com.hackybear.hungry_scan_core.dto.RecoveryInitDTO;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasswordRecoveryFlowTest {

    @Autowired
    private ApiJwtRequestUtils apiRequestUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private EmailService emailService;

    private static final String USERNAME = "matimemek@test.com";

    private String emailToken;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @Transactional
    @Rollback
    void completePasswordRecoveryFlow() throws Exception {
        sendPasswordRecoveryEmail();
        shouldNotUpdateWithNotEqualPasswords();
        shouldNotUpdateWithIncorrectPassword();
        shouldNotUpdateWithNonExistingToken();
        updateToNewPassword();
        loginWithNewPassword();
    }

    private void sendPasswordRecoveryEmail() throws Exception {
        User user = getDetachedUser();
        assertNull(user.getEmailToken());
        assertNull(user.getEmailTokenExpiry());
        RecoveryInitDTO recoveryInitDTO = new RecoveryInitDTO(USERNAME);
        apiRequestUtils.postAndExpect200("/api/user/recover", recoveryInitDTO);

        user = getDetachedUser();
        assertNotNull(user.getEmailToken());
        assertNotNull(user.getEmailTokenExpiry());
        this.emailToken = user.getEmailToken();
    }

    private void shouldNotUpdateWithNotEqualPasswords() throws Exception {
        RecoveryDTO recoveryDTO = new RecoveryDTO(
                this.emailToken,
                "Newpass123!",
                "Newpass123");
        Map<?, ?> response = apiRequestUtils.postAndExpectErrors("/api/user/confirm-recovery", recoveryDTO);
        assertEquals(1, response.size());
        assertEquals("Hasła nie są identyczne", response.get("repeatedPassword"));
    }

    private void shouldNotUpdateWithIncorrectPassword() throws Exception {
        RecoveryDTO recoveryDTO = new RecoveryDTO(
                this.emailToken,
                "newpass123",
                "newpass123");
        Map<?, ?> response = apiRequestUtils.postAndExpectErrors("/api/user/confirm-recovery", recoveryDTO);
        assertEquals(1, response.size());
        assertEquals(response.get("password"), "Hasło musi posiadać przynajmniej  jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny");
    }

    private void shouldNotUpdateWithNonExistingToken() throws Exception {
        RecoveryDTO recoveryDTO = new RecoveryDTO(
                "nonExistingEmailToken123123123123",
                "Newpass123!",
                "Newpass123!");
        Map<?, ?> response = apiRequestUtils.postAndExpectErrors("/api/user/confirm-recovery", recoveryDTO);
        assertEquals("Nieprawidłowy klucz przywracania hasła", response.get("error"));
    }

    private void updateToNewPassword() throws Exception {
        RecoveryDTO recoveryDTO = new RecoveryDTO(
                this.emailToken,
                "Newpass123!",
                "Newpass123!");
        apiRequestUtils.postAndExpect200("/api/user/confirm-recovery", recoveryDTO);
        User user = getDetachedUser();
        assertNull(user.getEmailToken());
        assertNull(user.getEmailTokenExpiry());
    }

    private void loginWithNewPassword() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO(USERNAME, "Newpass123!");
        Map<?, ?> response =
                apiRequestUtils.postAndFetchObject("/api/user/login", authRequestDTO, Map.class);
        assertEquals("/app", response.get("redirectUrl"));
        assertEquals("mati", response.get("forename"));
    }

    private User getDetachedUser() {
        User user = userRepository.findUserByUsername(USERNAME);
        entityManager.detach(user);
        return user;
    }
}
