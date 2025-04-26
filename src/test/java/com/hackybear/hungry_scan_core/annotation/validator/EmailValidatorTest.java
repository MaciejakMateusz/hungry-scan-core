package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import org.junit.jupiter.api.Test;
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
public class EmailValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidEmail_whenValidate_thenNoViolations() {
        List<String> emails = Arrays.asList("a@a.pl", "abc@abc.abc", "abcd.abcd@abcd.abc", "tEsT.123@test.com");
        expectNoViolations(emails, this::getRegistrationDTO);
    }

    @Test
    void givenInvalidEmail_whenValidate_thenExpectViolations() {
        List<String> invalidEmails = Arrays.asList("aabc@abc.a", "abc!@abc.com", "abc@abc!.pl", "abc", "test.com", "test%wrong.com", "test@test", "test@test.");
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.Email.message}",
                "propertyPath", "username");
        expectSpecificViolation(invalidEmails, this::getRegistrationDTO, params);
    }

    private RegistrationDTO getRegistrationDTO(String email) {
        return new RegistrationDTO(
                "Test",
                "Test",
                email,
                null,
                "Password123!",
                "Password123!");
    }
}
