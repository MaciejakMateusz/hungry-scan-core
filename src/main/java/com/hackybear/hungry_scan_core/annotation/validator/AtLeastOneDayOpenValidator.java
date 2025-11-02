package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.AtLeastOneDayOpen;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AtLeastOneDayOpenValidator implements ConstraintValidator<AtLeastOneDayOpen, Settings> {

    @Override
    public boolean isValid(Settings settings, ConstraintValidatorContext constraintValidatorContext) {
        if (settings == null || settings.getOperatingHours() == null || settings.getOperatingHours().isEmpty()) {
            return true;
        }
        return settings.getOperatingHours().values().stream().anyMatch(TimeRange::isAvailable);
    }
}
