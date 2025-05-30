package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLength;
import com.hackybear.hungry_scan_core.entity.Translatable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;
import java.util.stream.Stream;

public class LimitTranslationsLengthValidator implements ConstraintValidator<LimitTranslationsLength, Translatable> {

    @Override
    public boolean isValid(Translatable value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Stream.of(value.getPl(), value.getEn(), value.getFr(),
                        value.getDe(), value.getEs(), value.getUk())
                .filter(Objects::nonNull)
                .allMatch(s -> s.length() <= 255);
    }
}