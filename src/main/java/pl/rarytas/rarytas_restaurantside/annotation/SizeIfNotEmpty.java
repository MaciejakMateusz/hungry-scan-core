package pl.rarytas.rarytas_restaurantside.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.rarytas.rarytas_restaurantside.annotation.validator.SizeIfNotEmptyValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SizeIfNotEmptyValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SizeIfNotEmpty {
    String message() default "Opis kategorii musi mieć minimum 8 znaków";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
