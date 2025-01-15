package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlank;
import com.hackybear.hungry_scan_core.entity.Translatable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DefaultTranslationNotBlankValidator implements ConstraintValidator<DefaultTranslationNotBlank, Translatable> {

    @Override
    public void initialize(DefaultTranslationNotBlank constraintAnnotation) {
    }

    @Override
    public boolean isValid(Translatable value, ConstraintValidatorContext context) {
        String defaultTranslation = value.getDefaultTranslation();
        return defaultTranslation != null && !defaultTranslation.trim().isEmpty();
    }
}