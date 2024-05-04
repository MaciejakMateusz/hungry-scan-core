package pl.rarytas.hungry_scan_core.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.rarytas.hungry_scan_core.annotation.validator.CollectionNotEmptyValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CollectionNotEmptyValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectionNotEmpty {
    String message() default "{jakarta.validation.constraints.CollectionNotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
