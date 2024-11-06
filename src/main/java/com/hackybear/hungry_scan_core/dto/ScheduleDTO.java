package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.utility.TimeRange;

import java.time.DayOfWeek;
import java.util.Map;

public record ScheduleDTO(Long id, Long menuId, Map<DayOfWeek, TimeRange> plan) {
}
