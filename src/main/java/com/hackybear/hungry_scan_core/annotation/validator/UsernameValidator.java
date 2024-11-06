package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.Username;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

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
