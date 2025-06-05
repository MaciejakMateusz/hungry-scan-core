package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.utility.TimeRange;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Set;

public record MenuPlanDTO(Long id,
                          DayOfWeek dayOfWeek,
                          Set<TimeRange> timeRanges) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}