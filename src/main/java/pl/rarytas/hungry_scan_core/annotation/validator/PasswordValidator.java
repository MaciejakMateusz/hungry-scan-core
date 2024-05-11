package pl.rarytas.hungry_scan_core.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.hungry_scan_core.annotation.Password;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final Pattern PASSWORD_REGEX =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s])(?!.*\\s).{5,60}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return true;
        }
        Matcher matcher = PASSWORD_REGEX.matcher(value);
        return matcher.matches();
    }
}
