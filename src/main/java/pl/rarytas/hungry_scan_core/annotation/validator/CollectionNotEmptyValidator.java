package pl.rarytas.hungry_scan_core.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.rarytas.hungry_scan_core.annotation.CollectionNotEmpty;

import java.util.Collection;

public class CollectionNotEmptyValidator implements ConstraintValidator<CollectionNotEmpty, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && !value.isEmpty();
    }
}
