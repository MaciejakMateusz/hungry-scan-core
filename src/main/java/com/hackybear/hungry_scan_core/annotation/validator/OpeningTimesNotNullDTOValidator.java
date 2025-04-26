package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.OpeningTimesNotNullDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class OpeningTimesNotNullDTOValidator implements ConstraintValidator<OpeningTimesNotNullDTO, SettingsDTO> {

    @Override
    public boolean isValid(SettingsDTO value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) return true;
        return value.openingTime() != null && value.closingTime() != null;
    }
}