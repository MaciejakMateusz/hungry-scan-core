package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MenuDTO(Long id,
                      @NotBlank String name,
                      @NotBlank String colorHex,
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