package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;
import java.util.stream.Stream;

public class AnyTranslationNotBlankDTOValidator implements ConstraintValidator<AnyTranslationNotBlankDTO, TranslatableDTO> {

    @Override
    public boolean isValid(TranslatableDTO value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        return Stream.of(value.pl(), value.en(), value.fr(),
                        value.de(), value.es(), value.uk())
                .anyMatch(s -> s != null && !s.isBlank());
    }
}