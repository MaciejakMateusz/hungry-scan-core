package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.enums.Language;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

public record SettingsDTO(Long id,
                          Long restaurantId,

                          @NotNull
                          LocalTime openingTime,

                          @NotNull
                          LocalTime closingTime,

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