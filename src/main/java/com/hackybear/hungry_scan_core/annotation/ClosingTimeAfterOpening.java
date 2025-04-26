package com.hackybear.hungry_scan_core.annotation;

import com.hackybear.hungry_scan_core.annotation.validator.ClosingTimeAfterOpeningValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ClosingTimeAfterOpeningValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClosingTimeAfterOpening {
    String message() default "{jakarta.validation.constraints.ClosingTimeAfterOpeningTime.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}