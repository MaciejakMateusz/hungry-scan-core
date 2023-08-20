package pl.rarytas.rarytas_restaurantside.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.rarytas.rarytas_restaurantside.annotation.validator.UsernameExistsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UsernameExistsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameExists {
    String message() default "Użytkownik o podanej nazwie użytkownika istnieje";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
