package pl.rarytas.rarytas_restaurantside.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.SizeIfNotEmpty;

@Slf4j
public class SizeIfNotEmptyValidator implements ConstraintValidator<SizeIfNotEmpty, String> {

    @Override
    public void initialize(SizeIfNotEmpty constraintAnnotation) {
        log.info("Initializing @SizeIfNotEmpty annotation - provided description is not long enough");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || value.isEmpty() || value.length() >= 8;
    }
}
