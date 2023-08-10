package pl.rarytas.rarytas_restaurantside.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.rarytas.rarytas_restaurantside.annotation.validator.PaymentMethodValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PaymentMethodValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PaymentMethod {
    String message() default "{jakarta.validation.constraints.PaymentMethod.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
