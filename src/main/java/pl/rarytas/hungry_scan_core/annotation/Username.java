package pl.rarytas.hungry_scan_core.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.rarytas.hungry_scan_core.annotation.validator.UsernameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {
    String message() default "Nazwa użytkownika musi posiadać od 3 do 20 znaków i nie może zawierać spacji";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
