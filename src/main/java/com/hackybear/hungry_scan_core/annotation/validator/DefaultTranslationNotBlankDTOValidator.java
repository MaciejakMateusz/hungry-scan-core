package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class DefaultTranslationNotBlankDTOValidator implements ConstraintValidator<DefaultTranslationNotBlankDTO, TranslatableDTO> {

    @Override
    public void initialize(DefaultTranslationNotBlankDTO constraintAnnotation) {
    }

    @Override
    public boolean isValid(TranslatableDTO value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }

        String defaultTranslation = value.defaultTranslation();
        return defaultTranslation != null && !defaultTranslation.trim().isEmpty();
    }
}