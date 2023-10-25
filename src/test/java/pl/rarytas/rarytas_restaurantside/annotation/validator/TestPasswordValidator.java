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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPasswordValidator {

    private static final Pattern PASSWORD_REGEX =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{5,60}$");

    @Test
    public void shouldApprovePassword() {
        Matcher matcher = PASSWORD_REGEX.matcher("Example123?");
        assertTrue(matcher.matches());
    }

    @Test
    public void shouldNotApprovePassword() {

        String password1 = "Example123";
        String password2 = "example123!";
        String password3 = "EXAMPLE123!";
        String password4 = "example";
        String password5 = "123123123";

        Matcher matcher = PASSWORD_REGEX.matcher(password1);
        assertFalse(matcher.matches());
        matcher = PASSWORD_REGEX.matcher(password2);
        assertFalse(matcher.matches());
        matcher = PASSWORD_REGEX.matcher(password3);
        assertFalse(matcher.matches());
        matcher = PASSWORD_REGEX.matcher(password4);
        assertFalse(matcher.matches());
        matcher = PASSWORD_REGEX.matcher(password5);
        assertFalse(matcher.matches());
    }
}
