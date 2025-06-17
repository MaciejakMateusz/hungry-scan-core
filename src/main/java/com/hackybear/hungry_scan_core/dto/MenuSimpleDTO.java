package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public record MenuSimpleDTO(Long id,
                            Long restaurantId,

                            @NotBlank
                            String name,

                            @NotNull
                            MenuColorDTO color,

                            Set<MenuPlanDTO> plan,
                            boolean standard) implements Serializable, Comparable<MenuSimpleDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(MenuSimpleDTO other) {
        return this.name.compareTo(other.name);
    }

}
