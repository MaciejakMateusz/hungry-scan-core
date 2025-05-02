package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

public record DayTimeRangeDTO(Long id,
                              Long standardDayPlanId,
                              LocalTime startTime,
                              LocalTime endTime) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}