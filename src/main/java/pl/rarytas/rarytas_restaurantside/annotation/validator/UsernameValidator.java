package pl.rarytas.rarytas_restaurantside.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.Username;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UsernameValidator implements ConstraintValidator<Username, String> {

    private static final Pattern USERNAME_REGEX =
            Pattern.compile("^[a-zA-Z0-9_-]{3,20}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return true;
        }
        Matcher matcher = USERNAME_REGEX.matcher(value);
        return matcher.matches();
    }
}
