package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Map;

public record MenuSimpleDTO(Long id,

                            @NotBlank
                            String name,

                            Map<DayOfWeek, TimeRange> plan,
                            boolean standard) implements Serializable, Comparable<MenuSimpleDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(MenuSimpleDTO other) {
        return this.name.compareTo(other.name);
    }

}
