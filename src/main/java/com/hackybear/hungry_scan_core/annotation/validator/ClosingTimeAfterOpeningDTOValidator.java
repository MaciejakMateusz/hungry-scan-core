package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.ClosingTimeAfterOpeningDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class ClosingTimeAfterOpeningDTOValidator implements ConstraintValidator<ClosingTimeAfterOpeningDTO, SettingsDTO> {

    @Override
    public boolean isValid(SettingsDTO value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) return true;
        if (Objects.isNull(value.openingTime()) || Objects.isNull(value.closingTime())) return true;
        return value.closingTime().isAfter(value.openingTime());
    }
}