package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLength;
import com.hackybear.hungry_scan_core.entity.Translatable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class LimitTranslationsLengthValidator implements ConstraintValidator<LimitTranslationsLength, Translatable> {

    @Override
    public boolean isValid(Translatable value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (Objects.isNull(value.getDefaultTranslation()) || Objects.isNull(value.getTranslationEn())) {
            return true;
        }

        String defaultTranslation = value.getDefaultTranslation();
        String translationEn = value.getTranslationEn();
        return defaultTranslation.length() <= 255 && translationEn.length() <= 255;
    }
}