package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.ForenameSurname;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ForenameSurnameValidator implements ConstraintValidator<ForenameSurname, String> {

    private static final Pattern FORENAME_REGEX = Pattern.compile("^\\p{L}[\\p{L}\\-\\s']+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isBlank()) {
            return true;
        }
        Matcher matcher = FORENAME_REGEX.matcher(value);
        return matcher.matches();
    }
}
