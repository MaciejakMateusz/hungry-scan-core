package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

public record StatisticsDTO(
        long id,
        @NotNull
        long restaurantId,
        LocalTime rushHour,
        LocalTime avgStayTime,
        LocalTime avgWaitTime) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
