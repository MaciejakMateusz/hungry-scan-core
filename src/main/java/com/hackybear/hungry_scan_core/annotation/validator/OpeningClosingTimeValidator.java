package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.OpeningClosingTime;
import com.hackybear.hungry_scan_core.entity.Settings;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpeningClosingTimeValidator implements ConstraintValidator<OpeningClosingTime, Settings> {

    @Override
    public boolean isValid(Settings settings, ConstraintValidatorContext constraintValidatorContext) {
        if (settings == null || settings.getOperatingHours() == null || settings.getOperatingHours().isEmpty()) {
            return true;
        }
        return settings.getOperatingHours().values()
                .stream()
                .noneMatch(timeRange -> timeRange.getStartTime().equals(timeRange.getEndTime()));
    }
}
