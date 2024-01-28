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
class UsernameValidatorTest {

    private static final Pattern USERNAME_REGEX =
            Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");

    @Test
    public void shouldApprove() {
        String username1 = "user";
        String username2 = "UsEr";
        String username3 = "Us_Er";
        String username4 = "-user";
        String username5 = "u_s_e_r";
        String username6 = "1u2s_3-e4_5";
        String username7 = "_____";

        Matcher matcher = USERNAME_REGEX.matcher(username1);
        assertTrue(matcher.matches(), assertTrueMessage(username1));
        matcher = USERNAME_REGEX.matcher(username2);
        assertTrue(matcher.matches(), assertTrueMessage(username2));
        matcher = USERNAME_REGEX.matcher(username3);
        assertTrue(matcher.matches(), assertTrueMessage(username3));
        matcher = USERNAME_REGEX.matcher(username4);
        assertTrue(matcher.matches(), assertTrueMessage(username4));
        matcher = USERNAME_REGEX.matcher(username5);
        assertTrue(matcher.matches(), assertTrueMessage(username5));
        matcher = USERNAME_REGEX.matcher(username6);
        assertTrue(matcher.matches(), assertTrueMessage(username6));
        matcher = USERNAME_REGEX.matcher(username7);
        assertTrue(matcher.matches(), assertTrueMessage(username7));
    }

    @Test
    public void shouldNotApprove() {
        String username1 = "us";
        String username2 = "user?";
        String username3 = ";'][qew";
        Matcher matcher = USERNAME_REGEX.matcher(username1);
        assertFalse(matcher.matches(), assertFalseMessage(username1));
        matcher = USERNAME_REGEX.matcher(username2);
        assertFalse(matcher.matches(), assertFalseMessage(username2));
        matcher = USERNAME_REGEX.matcher(username3);
        assertFalse(matcher.matches(), assertFalseMessage(username3));
    }

    @Test
    public void shouldNotApproveSpaces() {
        String username1 = "use r";
        String username2 = "user ";
        String username3 = " user";
        String username4 = " ";
        Matcher matcher = USERNAME_REGEX.matcher(username1);
        assertFalse(matcher.matches(), assertFalseMessage(username1));
        matcher = USERNAME_REGEX.matcher(username2);
        assertFalse(matcher.matches(), assertFalseMessage(username2));
        matcher = USERNAME_REGEX.matcher(username3);
        assertFalse(matcher.matches(), assertFalseMessage(username3));
        matcher = USERNAME_REGEX.matcher(username4);
        assertFalse(matcher.matches(), assertFalseMessage(username4));
    }

    @Test
    public void shouldNotApproveEmpty() {
        String username = "";
        Matcher matcher = USERNAME_REGEX.matcher(username);
        assertFalse(matcher.matches(), assertFalseMessage(username));
    }

    @Test
    public void shouldApproveMinimumLength() {
        String username = "use";
        Matcher matcher = USERNAME_REGEX.matcher(username);
        assertTrue(matcher.matches(), assertTrueMessage(username));
    }

    @Test
    public void shouldApproveMaximumLength() {
        String username = "userUserUserUserUser";
        Matcher matcher = USERNAME_REGEX.matcher(username);
        assertTrue(matcher.matches(), assertTrueMessage(username));
    }

    private String assertFalseMessage(String input) {
        return "The username '" + input + "' was expected to be rejected, but it was approved.";
    }

    private String assertTrueMessage(String input) {
        return "The username '" + input + "' should be approved, but it was not.";
    }
}