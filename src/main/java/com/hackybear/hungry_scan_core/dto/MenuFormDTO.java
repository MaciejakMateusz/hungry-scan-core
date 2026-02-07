package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public record MenuFormDTO(Long id,

                          @NotBlank
                          @Length(min = 1, max = 100)
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
