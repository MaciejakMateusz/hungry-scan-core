package pl.rarytas.rarytas_restaurantside.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.PaymentMethod;

@Slf4j
public class PaymentMethodValidator implements ConstraintValidator<PaymentMethod, String> {

    @Override
    public void initialize(PaymentMethod constraintAnnotation) {
        log.info("Initializing @PaymentMethod annotation - provided payment method does not match the criteria");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        boolean isValidPaymentMethod = false;

        switch (value) {
            case "card", "cash" -> isValidPaymentMethod = true;
            default -> log.error("Invalid payment method chosen");
        }

        return isValidPaymentMethod;
    }
}
