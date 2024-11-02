package com.hackybear.hungry_scan_core.dto;

public record MenuSimpleDTO(Long id,
                            String name,
                            Long restaurantId,
                            ScheduleDTO schedule,
                            boolean isAllDay) {
}
