package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record MenuSimpleDTO(Long id,

                            @NotBlank
                            String name,

                            ScheduleDTO schedule,
                            boolean standard) implements Serializable, Comparable<MenuSimpleDTO> {

    @Override
    public int compareTo(MenuSimpleDTO other) {
        return this.name.compareTo(other.name);
    }

}
