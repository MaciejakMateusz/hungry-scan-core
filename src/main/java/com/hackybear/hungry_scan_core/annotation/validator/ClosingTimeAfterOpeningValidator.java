package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.ClosingTimeAfterOpening;
import com.hackybear.hungry_scan_core.entity.Settings;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ClosingTimeAfterOpeningValidator implements ConstraintValidator<ClosingTimeAfterOpening, Settings> {

    @Override
    public boolean isValid(Settings value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) return true;
        if (Objects.isNull(value.getOpeningTime()) || Objects.isNull(value.getClosingTime())) return true;
        return value.getClosingTime().isAfter(value.getOpeningTime());
    }
}