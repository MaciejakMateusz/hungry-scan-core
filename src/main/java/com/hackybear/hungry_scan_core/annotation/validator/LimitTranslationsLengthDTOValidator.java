package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;
import java.util.stream.Stream;

public class LimitTranslationsLengthDTOValidator implements ConstraintValidator<LimitTranslationsLengthDTO, TranslatableDTO> {

    @Override
    public boolean isValid(TranslatableDTO value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Stream.of(value.pl(), value.en(), value.fr(),
                        value.de(), value.es(), value.uk())
                .filter(Objects::nonNull)
                .allMatch(s -> s.length() <= 255);
    }
}