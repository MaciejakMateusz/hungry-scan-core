package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class LimitTranslationsLengthDTOValidator implements ConstraintValidator<LimitTranslationsLengthDTO, TranslatableDTO> {

    @Override
    public boolean isValid(TranslatableDTO value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (Objects.isNull(value.defaultTranslation()) || Objects.isNull(value.translationEn())) {
            return true;
        }

        String defaultTranslation = value.defaultTranslation();
        String translationEn = value.translationEn();
        return defaultTranslation.length() <= 255 && translationEn.length() <= 255;
    }
}