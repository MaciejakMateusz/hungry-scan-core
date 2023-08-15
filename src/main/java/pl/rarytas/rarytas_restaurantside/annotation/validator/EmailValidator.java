package pl.rarytas.rarytas_restaurantside.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.Email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EmailValidator implements ConstraintValidator<Email, String> {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.([a-zA-Z]{2,})$");

    @Override
    public void initialize(Email constraintAnnotation) {
        log.info("Initializing @Email annotation - provided email does not match the regex");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return true;
        }
        Matcher matcher = EMAIL_REGEX.matcher(value);
        return matcher.matches();
    }
}
