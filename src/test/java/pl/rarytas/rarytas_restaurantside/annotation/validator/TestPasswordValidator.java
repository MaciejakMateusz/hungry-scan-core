package pl.rarytas.rarytas_restaurantside.annotation.validator;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPasswordValidator {

    private static final Pattern PASSWORD_REGEX =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s])(?!.*\\s).{5,60}$");

    @Test
    public void shouldApprove() {
        String password = "Example123!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches(), "The password '" + password + "' should be approved, but it was not.");
    }

    @Test
    public void shouldNotApprove() {

        String password1 = "Example123";
        String password2 = "example123!";
        String password3 = "EXAMPLE123!";
        String password4 = "example";
        String password5 = "123123123";
        String password6 = "EXAMPLE";
        String password7 = "!@#$$#!@^$";
        String password8 = "example123";
        String password9 = "!!!!!!!!!";

        Matcher matcher = PASSWORD_REGEX.matcher(password1);
        assertFalse(matcher.matches(), "The password '" + password1 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password2);
        assertFalse(matcher.matches(), "The password '" + password2 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password3);
        assertFalse(matcher.matches(), "The password '" + password3 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password4);
        assertFalse(matcher.matches(), "The password '" + password4 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password5);
        assertFalse(matcher.matches(), "The password '" + password5 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password6);
        assertFalse(matcher.matches(), "The password '" + password6 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password7);
        assertFalse(matcher.matches(), "The password '" + password7 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password8);
        assertFalse(matcher.matches(), "The password '" + password8 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password9);
        assertFalse(matcher.matches(), "The password '" + password9 + "' was expected to be rejected, but it was approved.");
    }

    @Test
    public void shouldApproveMinimumLength() {
        String password = "Min1!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches(), "The password '" + password + "' should be approved, but it was not.");
    }

    @Test
    public void shouldApproveMaximumLength() {
        String password = "Aa1!Bb2@C3dDeE4fFgG5hHiI6jJkKlLmMnNoO7pPqQrRsStTuUvVwWxXyYzZ";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches(), "The password '" + password + "' should be approved, but it was not.");
    }

    @Test
    public void shouldNotApproveLength() {
        String password1 = "Mi1!";
        String password2 = "Aa1!Bb2@C3dDeE4fFgG5hHiI6jJkKlLmMnNoO7pPqQrRsStTuUvVwWxXyYzZ!";
        Matcher matcher = PASSWORD_REGEX.matcher(password1);
        assertFalse(matcher.matches(), "The password '" + password1 + "' was expected to be rejected, but it was approved.");
        matcher = PASSWORD_REGEX.matcher(password2);
        assertFalse(matcher.matches(), "The password '" + password2 + "' was expected to be rejected, but it was approved.");
    }

    @Test
    public void shouldNotApproveEmpty() {
        String password = "";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertFalse(matcher.matches(), "The password '" + password + "' was expected to be rejected, but it was approved.");
    }

    @Test
    public void shouldNotApproveContainingSpaces() {
        String password = "Example 123!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertFalse(matcher.matches(), "The password '" + password + "' was expected to be rejected, but it was approved.");
    }
}
