package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MenuDTO(Long id,
                      @NotBlank String name,
                      @NotNull MenuColorDTO color,
                      List<CategoryDTO> categories,
                      Set<MenuPlanDTO> plan,
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