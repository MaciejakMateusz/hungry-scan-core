package com.hackybear.hungry_scan_core.annotation;

import com.hackybear.hungry_scan_core.annotation.validator.LimitTranslationsLengthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LimitTranslationsLengthValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitTranslationsLength {
    String message() default "{jakarta.validation.constraints.LimitTranslationsLength.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}