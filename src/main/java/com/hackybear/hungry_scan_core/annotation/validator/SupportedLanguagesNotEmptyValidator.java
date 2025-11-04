package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.SupportedLanguagesNotEmpty;
import com.hackybear.hungry_scan_core.entity.Settings;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportedLanguagesNotEmptyValidator implements ConstraintValidator<SupportedLanguagesNotEmpty, Settings> {

    @Override
    public boolean isValid(Settings settings, ConstraintValidatorContext constraintValidatorContext) {
        if (settings == null ||
                settings.getSupportedLanguages() == null) {
            return true;
        }
        return !settings.getSupportedLanguages().isEmpty();
    }
}