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
public class PasswordValidatorTest {

    private static final Pattern PASSWORD_REGEX =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s])(?!.*\\s).{5,60}$");

    @Test
    public void shouldApprove() {
        String password = "Example123!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches());
    }

    @Test
    public void shouldNotApprove() {
        List<String> wrongPasswords = Arrays.asList("Example123", "example123!", "EXAMPLE123!", "example", "123123123",
                "EXAMPLE", "!@#$$#!@^$", "example123", "!!!!!!!!!");
        for (String password : wrongPasswords) {
            Matcher matcher = PASSWORD_REGEX.matcher(password);
            assertFalse(matcher.matches());
        }
    }

    @Test
    public void shouldApproveMinimumLength() {
        String password = "Min1!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches());
    }

    @Test
    public void shouldApproveMaximumLength() {
        String password = "Aa1!Bb2@C3dDeE4fFgG5hHiI6jJkKlLmMnNoO7pPqQrRsStTuUvVwWxXyYzZ";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches());
    }

    @Test
    public void shouldNotApproveLength() {
        String password1 = "Mi1!";
        String password2 = "Aa1!Bb2@C3dDeE4fFgG5hHiI6jJkKlLmMnNoO7pPqQrRsStTuUvVwWxXyYzZ!";
        Matcher matcher = PASSWORD_REGEX.matcher(password1);
        assertFalse(matcher.matches());
        matcher = PASSWORD_REGEX.matcher(password2);
        assertFalse(matcher.matches());
    }

    @Test
    public void shouldNotApproveEmpty() {
        String password = "";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertFalse(matcher.matches());
    }

    @Test
    public void shouldNotApproveContainingSpaces() {
        String password = "Example 123!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertFalse(matcher.matches());
    }
}
