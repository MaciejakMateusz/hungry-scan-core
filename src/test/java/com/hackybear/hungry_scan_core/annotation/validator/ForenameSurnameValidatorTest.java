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
public class ForenameSurnameValidatorTest extends ValidatorTestBase {

    @Test
    void givenValidForename_whenValidate_thenNoViolations() {
        List<String> forenames =
                Arrays.asList("Ernest", "Al", "mAtI", "MATI", "qweqweQQWEQWEQWEQWEQWEQWEQWEQWEQWEQEWQWEQWEQ", "Zażółć");
        expectNoViolations(forenames, this::getRegistrationDTO);
    }

    @Test
    void givenInvalidForename_whenValidate_thenExpectViolations() {
        List<String> invalidForenames = Arrays.asList("a", "A", "A1", "A!", ",,", "12", "Al1", "Ernest!");
        Map<String, String> params = Map.of(
                "messageTemplate", "{jakarta.validation.constraints.ForenameSurname.message}",
                "propertyPath", "forename");
        expectSpecificViolation(invalidForenames, this::getRegistrationDTO, params);
    }

    private RegistrationDTO getRegistrationDTO(String forename) {
        return new RegistrationDTO(
                forename,
                "Test",
                "test@test.com",
                null,
                "Password123!",
                "Password123!");
    }
}
