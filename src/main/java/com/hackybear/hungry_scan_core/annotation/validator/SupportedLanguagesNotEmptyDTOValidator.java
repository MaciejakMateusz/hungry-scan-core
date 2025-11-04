package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.SupportedLanguagesNotEmptyDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportedLanguagesNotEmptyDTOValidator implements ConstraintValidator<SupportedLanguagesNotEmptyDTO, SettingsDTO> {

    @Override
    public boolean isValid(SettingsDTO settings, ConstraintValidatorContext constraintValidatorContext) {
        if (settings == null ||
                settings.supportedLanguages() == null) {
            return true;
        }
        return !settings.supportedLanguages().isEmpty();
    }
}