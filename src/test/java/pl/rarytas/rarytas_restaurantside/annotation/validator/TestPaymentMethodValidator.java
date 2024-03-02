package pl.rarytas.rarytas_restaurantside.annotation.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;

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
        return Arrays.stream(PaymentMethod.values())
                .anyMatch(paymentMethod -> paymentMethod.getMethodName().equalsIgnoreCase(value));
    }

    @Test
    public void shouldApprove() {
        String paymentMethod1 = "cash";
        String paymentMethod2 = "card";
        String paymentMethod3 = "online";
        assertTrue(isValidPaymentMethod(paymentMethod1));
        assertTrue(isValidPaymentMethod(paymentMethod2));
        assertTrue(isValidPaymentMethod(paymentMethod3));
    }

    @Test
    public void shouldNotApprove() {
        String paymentMethod1 = "money";
        String paymentMethod2 = "hajs";
        String paymentMethod3 = "mamona";
        assertFalse(isValidPaymentMethod(paymentMethod1));
        assertFalse(isValidPaymentMethod(paymentMethod2));
        assertFalse(isValidPaymentMethod(paymentMethod3));
    }
}