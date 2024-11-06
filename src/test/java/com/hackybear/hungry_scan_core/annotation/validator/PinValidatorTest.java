package com.hackybear.hungry_scan_core.annotation.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PinValidatorTest {

    private static final Pattern PIN_REGEX = Pattern.compile("^\\d{4}$");

    @Test
    public void shouldApprove() {
        String password = "2351";
        Matcher matcher = PIN_REGEX.matcher(password);
        assertTrue(matcher.matches());
    }

    @Test
    public void shouldNotApprove() {
        List<String> wrongPins = Arrays.asList("", "1", "12", "123", "123c", "12345", "123!", "!!!!", "abcd");
        for (String wrongPin : wrongPins) {
            Matcher matcher = PIN_REGEX.matcher(wrongPin);
            assertFalse(matcher.matches());
        }
    }

}
