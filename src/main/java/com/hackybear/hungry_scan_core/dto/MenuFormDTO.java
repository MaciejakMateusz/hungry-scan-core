package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record MenuFormDTO(Long id,

                          @NotBlank
                          String name,

                          @NotNull
                          MenuColorDTO color) implements Serializable, Comparable<MenuFormDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(MenuFormDTO other) {
        return this.name.compareTo(other.name);
    }

}
