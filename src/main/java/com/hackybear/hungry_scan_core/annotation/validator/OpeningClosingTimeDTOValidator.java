package com.hackybear.hungry_scan_core.annotation.validator;

import com.hackybear.hungry_scan_core.annotation.OpeningClosingTimeDTO;
import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpeningClosingTimeDTOValidator implements ConstraintValidator<OpeningClosingTimeDTO, SettingsDTO> {

    @Override
    public boolean isValid(SettingsDTO settings, ConstraintValidatorContext constraintValidatorContext) {
        if (settings == null || settings.operatingHours() == null || settings.operatingHours().isEmpty()) {
            return true;
        }
        return settings.operatingHours().values()
                .stream()
                .noneMatch(timeRange -> timeRange.getStartTime().equals(timeRange.getEndTime()));
    }
}
