package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

public record RestaurantSimpleDTO(Long id,
                                  @NotBlank String name) implements Serializable, Comparable<RestaurantSimpleDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(RestaurantSimpleDTO other) {
        return this.name.compareTo(other.name);
    }
}

