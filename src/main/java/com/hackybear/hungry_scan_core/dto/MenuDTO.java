package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record MenuDTO(Long id,
                      @NotBlank String name,
                      List<CategoryDTO> categories,
                      Map<DayOfWeek, TimeRange> plan,
                      boolean standard,
                      LocalDateTime created,
                      LocalDateTime updated,
                      String modifiedBy,
                      String createdBy) implements Serializable, Comparable<MenuDTO> {

    @Override
    public int compareTo(MenuDTO other) {
        return this.name.compareTo(other.name);
    }

}