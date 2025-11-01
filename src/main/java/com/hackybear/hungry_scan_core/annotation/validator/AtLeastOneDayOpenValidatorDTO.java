package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.AtLeastOneDayOpenDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AtLeastOneDayOpenValidatorDTO implements ConstraintValidator<AtLeastOneDayOpenDTO, SettingsDTO> {

    @Override
    public boolean isValid(SettingsDTO settings, ConstraintValidatorContext constraintValidatorContext) {
        if (settings.operatingHours() == null || settings.operatingHours().isEmpty()) {
            return true;
        }
        return settings.operatingHours().values().stream().anyMatch(TimeRange::isAvailable);
    }
}
