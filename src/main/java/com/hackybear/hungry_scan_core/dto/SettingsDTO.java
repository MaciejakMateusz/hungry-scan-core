package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.utility.TimeRange;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Map;

public record SettingsDTO(Long id,
                          Long restaurantId,
                          Map<DayOfWeek, TimeRange> operatingHours,
                          Long bookingDuration,
                          Language language,
                          Long employeeSessionTime,
                          Long customerSessionTime,
                          Short capacity,
                          boolean orderCommentAllowed,
                          boolean waiterCommentAllowed) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}