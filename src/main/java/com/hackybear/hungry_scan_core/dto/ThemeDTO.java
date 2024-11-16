package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record ThemeDTO(
        long id,

        @NotNull
        long restaurantId,

        @NotBlank
        String name,

        boolean active) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
