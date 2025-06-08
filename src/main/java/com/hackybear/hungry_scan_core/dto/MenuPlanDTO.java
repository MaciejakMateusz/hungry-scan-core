package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.utility.TimeRange;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Set;
import java.util.UUID;

public record MenuPlanDTO(UUID id,
                          Long menuId,
                          DayOfWeek dayOfWeek,
                          Set<TimeRange> timeRanges) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}