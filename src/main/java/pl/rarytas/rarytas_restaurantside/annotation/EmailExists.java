package pl.rarytas.rarytas_restaurantside.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.rarytas.rarytas_restaurantside.annotation.validator.EmailExistsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailExistsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailExists {
    String message() default "Użytkownik o podanym adresie email już istnieje";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
