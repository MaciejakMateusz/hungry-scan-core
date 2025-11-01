package com.hackybear.hungry_scan_core.annotation.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorTestBase {

    protected static Validator validator;

    public ValidatorTestBase() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    protected static <T, R> void expectNoViolations(Collection<R> values, Function<R, T> function) {
        values.forEach(value -> {
            T t = function.apply(value);
            Set<ConstraintViolation<T>> violations = validator.validate(t);
            assertTrue(violations.isEmpty());
        });
    }

    protected static <T> void expectNoViolations(T value) {
        Set<ConstraintViolation<T>> violations = validator.validate(value);
        assertTrue(violations.isEmpty());
    }

    protected static <T> void expectSpecificViolation(List<String> values,
                                                      Function<String, T> function,
                                                      Map<String, String> params) {
        values.forEach(value -> {
            T registration = function.apply(value);
            Set<ConstraintViolation<T>> violations = validator.validate(registration);
            assertEquals(1, violations.size());
            Optional<ConstraintViolation<T>> optViolation = violations.stream().findFirst();
            if (optViolation.isPresent()) {
                ConstraintViolation<T> violation = optViolation.get();
                assertEquals(params.get("messageTemplate"), violation.getMessageTemplate());
                assertEquals(params.get("propertyPath"), violation.getPropertyPath().toString());
            }
        });
    }

    protected static <T> void expectSpecificViolation(T value,
                                                      Map<String, String> params) {
        Set<ConstraintViolation<T>> violations = validator.validate(value);
        assertEquals(1, violations.size());
        Optional<ConstraintViolation<T>> optViolation = violations.stream().findFirst();
        if (optViolation.isPresent()) {
            ConstraintViolation<T> violation = optViolation.get();
            assertEquals(params.get("messageTemplate"), violation.getMessageTemplate());
            assertEquals(params.get("propertyPath"), violation.getPropertyPath().toString());
        }
    }
}
