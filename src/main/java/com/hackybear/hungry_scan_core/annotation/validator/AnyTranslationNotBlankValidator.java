package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlank;
import com.hackybear.hungry_scan_core.entity.Translatable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;
import java.util.stream.Stream;

public class AnyTranslationNotBlankValidator implements ConstraintValidator<AnyTranslationNotBlank, Translatable> {

    @Override
    public boolean isValid(Translatable value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        return Stream.of(value.getPl(), value.getEn(), value.getFr(),
                        value.getDe(), value.getEs(), value.getUk())
                .anyMatch(s -> s != null && !s.isBlank());
    }
}