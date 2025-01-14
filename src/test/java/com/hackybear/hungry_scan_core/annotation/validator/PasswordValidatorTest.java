package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PasswordValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidEmail_whenValidate_thenNoViolations() {
        List<String> passwords = Arrays.asList("Aa1!Bb2@C3dDeE4fFgG5hHiI6jJkKlLmMnNoO7pPqQrRsStTuUvVwWxXyYzZ",
                "Password123!", "Pass321^", "CzaryMary7)");
        expectNoViolations(passwords, this::getRegistrationDTO);
    }

    @Test
    void givenInvalidEmail_whenValidate_thenExpectViolations() {
        List<String> passwords = Arrays.asList("Example123", "example123!", "EXAMPLE123!", "example", "123123123",
                "EXAMPLE", "!@#$$#!@^$", "example123", "!!!!!!!!!", "Example 123!", "Mi1!",
                "Aa1!Bb2@C3dDeE4fFgG5hHiI6jJkKlLmMnNoO7pPqQrRsStTuUvVwWxXyYzZ!");
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.Password.message}",
                "propertyPath", "password");
        expectSpecificViolation(passwords, this::getRegistrationDTO, params);
    }

    private RegistrationDTO getRegistrationDTO(String password) {
        return new RegistrationDTO(
                "Test",
                "Test",
                "test@test.com",
                null,
                password,
                password);
    }
}
