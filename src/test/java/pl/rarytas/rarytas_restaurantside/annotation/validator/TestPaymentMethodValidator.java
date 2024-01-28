package pl.rarytas.rarytas_restaurantside.annotation.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.utility.PaymentMethodEnum;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestPaymentMethodValidator {

    private static boolean isValidPaymentMethod(String value) {
        return Arrays.stream(PaymentMethodEnum.values())
                .anyMatch(paymentMethod -> paymentMethod.getMethodName().equalsIgnoreCase(value));
    }

    @Test
    public void shouldApprove() {
        String paymentMethod1 = "cash";
        String paymentMethod2 = "card";
        String paymentMethod3 = "online";
        assertTrue(isValidPaymentMethod(paymentMethod1), assertTrueMessage(paymentMethod1));
        assertTrue(isValidPaymentMethod(paymentMethod2), assertTrueMessage(paymentMethod2));
        assertTrue(isValidPaymentMethod(paymentMethod3), assertTrueMessage(paymentMethod3));
    }

    @Test
    public void shouldNotApprove() {
        String paymentMethod1 = "money";
        String paymentMethod2 = "hajs";
        String paymentMethod3 = "mamona";
        assertFalse(isValidPaymentMethod(paymentMethod1), assertFalseMessage(paymentMethod1));
        assertFalse(isValidPaymentMethod(paymentMethod2), assertFalseMessage(paymentMethod2));
        assertFalse(isValidPaymentMethod(paymentMethod3), assertFalseMessage(paymentMethod3));
    }

    private String assertFalseMessage(String input) {
        return "The payment method '" + input + "' was expected to be rejected, but it was approved.";
    }

    private String assertTrueMessage(String input) {
        return "The payment method '" + input + "' should be approved, but it was not.";
    }
}