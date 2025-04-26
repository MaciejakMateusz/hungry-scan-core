package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.OpeningTimesNotNull;
import com.hackybear.hungry_scan_core.entity.Settings;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class OpeningTimesNotNullValidator implements ConstraintValidator<OpeningTimesNotNull, Settings> {

    @Override
    public boolean isValid(Settings value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) return true;
        return value.getOpeningTime() != null && value.getClosingTime() != null;
    }
}