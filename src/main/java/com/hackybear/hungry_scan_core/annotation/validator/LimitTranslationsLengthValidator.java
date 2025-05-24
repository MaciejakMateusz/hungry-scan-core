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

        String defaultTranslation = value.getPl();
        String translationEn = value.getEn();
        if (Objects.nonNull(defaultTranslation) && Objects.isNull(translationEn)) {
            return defaultTranslation.length() <= 255;
        } else if (Objects.nonNull(value.getPl())) {
            return defaultTranslation.length() <= 255 && translationEn.length() <= 255;
        }

        if (Objects.nonNull(value.getEn())) {
            return translationEn.length() <= 255;
        } else {
            return true;
        }
    }
}