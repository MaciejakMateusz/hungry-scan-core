package pl.rarytas.rarytas_restaurantside.annotation.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
public class TestPasswordValidator {

    private static final Pattern PASSWORD_REGEX =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s])(?!.*\\s).{5,60}$");

    @Test
    public void shouldApprove() {
        String password = "Example123!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches(), assertTrueMessage(password));
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
        assertFalse(matcher.matches(), assertFalseMessage(password1));
        matcher = PASSWORD_REGEX.matcher(password2);
        assertFalse(matcher.matches(), assertFalseMessage(password2));
        matcher = PASSWORD_REGEX.matcher(password3);
        assertFalse(matcher.matches(), assertFalseMessage(password3));
        matcher = PASSWORD_REGEX.matcher(password4);
        assertFalse(matcher.matches(), assertFalseMessage(password4));
        matcher = PASSWORD_REGEX.matcher(password5);
        assertFalse(matcher.matches(), assertFalseMessage(password5));
        matcher = PASSWORD_REGEX.matcher(password6);
        assertFalse(matcher.matches(), assertFalseMessage(password6));
        matcher = PASSWORD_REGEX.matcher(password7);
        assertFalse(matcher.matches(), assertFalseMessage(password7));
        matcher = PASSWORD_REGEX.matcher(password8);
        assertFalse(matcher.matches(), assertFalseMessage(password8));
        matcher = PASSWORD_REGEX.matcher(password9);
        assertFalse(matcher.matches(), assertFalseMessage(password9));
    }

    @Test
    public void shouldApproveMinimumLength() {
        String password = "Min1!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches(), assertTrueMessage(password));
    }

    @Test
    public void shouldApproveMaximumLength() {
        String password = "Aa1!Bb2@C3dDeE4fFgG5hHiI6jJkKlLmMnNoO7pPqQrRsStTuUvVwWxXyYzZ";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertTrue(matcher.matches(), assertTrueMessage(password));
    }

    @Test
    public void shouldNotApproveLength() {
        String password1 = "Mi1!";
        String password2 = "Aa1!Bb2@C3dDeE4fFgG5hHiI6jJkKlLmMnNoO7pPqQrRsStTuUvVwWxXyYzZ!";
        Matcher matcher = PASSWORD_REGEX.matcher(password1);
        assertFalse(matcher.matches(), assertFalseMessage(password1));
        matcher = PASSWORD_REGEX.matcher(password2);
        assertFalse(matcher.matches(), assertFalseMessage(password2));
    }

    @Test
    public void shouldNotApproveEmpty() {
        String password = "";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertFalse(matcher.matches(), assertFalseMessage(password));
    }

    @Test
    public void shouldNotApproveContainingSpaces() {
        String password = "Example 123!";
        Matcher matcher = PASSWORD_REGEX.matcher(password);
        assertFalse(matcher.matches(), assertFalseMessage(password));
    }

    private String assertFalseMessage(String input) {
        return "The password '" + input + "' was expected to be rejected, but it was approved.";
    }

    private String assertTrueMessage(String input) {
        return "The password '" + input + "' should be approved, but it was not.";
    }
}
