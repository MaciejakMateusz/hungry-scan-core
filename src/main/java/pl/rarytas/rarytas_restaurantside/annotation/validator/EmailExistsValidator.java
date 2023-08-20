package pl.rarytas.rarytas_restaurantside.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.EmailExists;
import pl.rarytas.rarytas_restaurantside.service.RegisterService;

@Slf4j
public class EmailExistsValidator implements ConstraintValidator<EmailExists, String> {

    private final RegisterService registerService;

    public EmailExistsValidator(RegisterService registerService) {
        this.registerService = registerService;
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return !registerService.existsByEmail(value);
    }
}
