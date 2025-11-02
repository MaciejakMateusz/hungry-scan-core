package com.hackybear.hungry_scan_core.annotation;

import com.hackybear.hungry_scan_core.annotation.validator.OpeningClosingTimeDTOValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OpeningClosingTimeDTOValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpeningClosingTimeDTO {
    String message() default "{jakarta.validation.constraints.OpeningClosingTime.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
