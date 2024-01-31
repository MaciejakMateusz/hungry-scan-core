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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestEmailValidator {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.([a-zA-Z]{2,})$");

    @Test
    public void shouldApprove() {
        String email = "example@example.com";
        Matcher matcher = EMAIL_REGEX.matcher(email);
        assertTrue(matcher.matches());
    }

    @Test
    public void shouldNotApprove() {

        String email1 = "example@example.";
        String email2 = "example@example";
        String email3 = "example@";
        String email4 = "example@??.??";
        String email5 = "example@example,com";

        Matcher matcher = EMAIL_REGEX.matcher(email1);
        assertFalse(matcher.matches());
        matcher = EMAIL_REGEX.matcher(email2);
        assertFalse(matcher.matches());
        matcher = EMAIL_REGEX.matcher(email3);
        assertFalse(matcher.matches());
        matcher = EMAIL_REGEX.matcher(email4);
        assertFalse(matcher.matches());
        matcher = EMAIL_REGEX.matcher(email5);
        assertFalse(matcher.matches());
    }

    @Test
    public void shouldNotApproveEmpty() {
        String email = "";
        Matcher matcher = EMAIL_REGEX.matcher(email);
        assertFalse(matcher.matches());
    }

    @Test
    public void shouldApproveLocalSingleCharacter() {
        String email = "a@example.com";
        Matcher matcher = EMAIL_REGEX.matcher(email);
        assertTrue(matcher.matches());
    }

    @Test
    public void shouldNotApproveDomainSingleCharacters() {
        String email = "example@a.c";
        Matcher matcher = EMAIL_REGEX.matcher(email);
        assertFalse(matcher.matches());
    }

    @Test
    public void shouldNotApproveSpaceCharacterInLocal() {
        String email = "example @example.com";
        Matcher matcher = EMAIL_REGEX.matcher(email);
        assertFalse(matcher.matches());
    }

    @Test
    public void shouldNotApproveSpaceCharacterInDomain() {
        String email = "example@ex ample.com";
        Matcher matcher = EMAIL_REGEX.matcher(email);
        assertFalse(matcher.matches());
    }
}
