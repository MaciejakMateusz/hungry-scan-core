package pl.rarytas.rarytas_restaurantside.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;

import java.util.Arrays;

@Slf4j
public class PaymentMethodValidator implements ConstraintValidator<pl.rarytas.rarytas_restaurantside.annotation.PaymentMethod, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank() || "Brak".equals(value)) {
            return true;
        }

        if (!isValidPaymentMethod(value)) {
            log.error("Invalid payment method chosen");
        }

        return isValidPaymentMethod(value);
    }

    private static boolean isValidPaymentMethod(String value) {
        return Arrays.stream(PaymentMethod.values())
                .anyMatch(paymentMethod -> paymentMethod.getMethodName().equalsIgnoreCase(value));
    }
}
