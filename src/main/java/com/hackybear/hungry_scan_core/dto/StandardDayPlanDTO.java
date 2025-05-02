package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.List;

public record StandardDayPlanDTO(Long id,
                                 Long menuId,
                                 DayOfWeek dayOfWeek,
                                 List<DayTimeRangeDTO> timeRanges) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}