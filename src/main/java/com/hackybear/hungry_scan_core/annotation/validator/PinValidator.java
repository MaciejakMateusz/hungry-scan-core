package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.Pin;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PinValidator implements ConstraintValidator<Pin, String> {

    private static final Pattern PIN_REGEX = Pattern.compile("^\\d{4}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return true;
        }
        Matcher matcher = PIN_REGEX.matcher(value);
        return matcher.matches();
    }
}
