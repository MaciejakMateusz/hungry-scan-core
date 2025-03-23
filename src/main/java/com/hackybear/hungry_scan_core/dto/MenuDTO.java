package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;

public record MenuDTO(Long id,
                      @NotBlank String name,
                      List<CategoryDTO> categories,
                      ScheduleDTO schedule,
                      boolean standard) implements Serializable, Comparable<MenuDTO> {

    @Override
    public int compareTo(MenuDTO other) {
        return this.name.compareTo(other.name);
    }

}