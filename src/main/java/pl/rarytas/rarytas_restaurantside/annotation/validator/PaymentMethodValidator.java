package pl.rarytas.rarytas_restaurantside.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.PaymentMethod;

@Slf4j
public class PaymentMethodValidator implements ConstraintValidator<PaymentMethod, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return true;
        }
        boolean isValidPaymentMethod = false;

        switch (value) {
            case "card", "cash", "online" -> isValidPaymentMethod = true;
            default -> log.error("Invalid payment method chosen");
        }

        return isValidPaymentMethod;
    }
}
