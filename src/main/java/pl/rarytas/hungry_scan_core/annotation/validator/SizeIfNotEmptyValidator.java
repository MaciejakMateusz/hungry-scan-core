package pl.rarytas.hungry_scan_core.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.hungry_scan_core.annotation.SizeIfNotEmpty;

@Slf4j
public class SizeIfNotEmptyValidator implements ConstraintValidator<SizeIfNotEmpty, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || value.isBlank() || value.length() >= 8;
    }
}
