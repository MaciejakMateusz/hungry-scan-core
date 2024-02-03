package pl.rarytas.rarytas_restaurantside.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.rarytas.rarytas_restaurantside.annotation.CollectionNotEmpty;

import java.util.Collection;

public class CollectionNotEmptyValidator implements ConstraintValidator<CollectionNotEmpty, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && !value.isEmpty();
    }
}
