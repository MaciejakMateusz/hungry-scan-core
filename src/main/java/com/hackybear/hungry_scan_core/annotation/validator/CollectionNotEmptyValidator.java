package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.CollectionNotEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class CollectionNotEmptyValidator implements ConstraintValidator<CollectionNotEmpty, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && !value.isEmpty();
    }
}
