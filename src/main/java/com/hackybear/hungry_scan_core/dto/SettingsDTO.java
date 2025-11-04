package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;

public record SettingsDTO(Long id,
                          Long restaurantId,
                          Map<DayOfWeek, TimeRange> operatingHours,
                          Long bookingDuration,
                          @NotNull
                          Language language,

                          @NotEmpty
                          @NotNull
                          Set<Language> supportedLanguages,

                          Long employeeSessionTime,
                          Long customerSessionTime,
                          Short capacity,
                          boolean orderCommentAllowed,
                          boolean waiterCommentAllowed) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}