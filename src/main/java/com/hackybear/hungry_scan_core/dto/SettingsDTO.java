package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.enums.Language;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SettingsDTO(Long restaurantId,

                          @NotNull
                          LocalTime openingTime,

                          @NotNull
                          LocalTime closingTime,

                          @NotNull
                          Long bookingDuration,

                          @NotNull
                          Language language,

                          @NotNull
                          Long employeeSessionTime,

                          @NotNull
                          Long customerSessionTime,

                          @NotNull
                          Short capacity,

                          boolean orderCommentAllowed,
                          boolean waiterCommentAllowed) {
}