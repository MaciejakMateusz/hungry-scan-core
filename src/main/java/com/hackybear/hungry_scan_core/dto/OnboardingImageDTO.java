package com.hackybear.hungry_scan_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record OnboardingImageDTO(
        long id,

        @NotNull
        long restaurantId,

        @NotBlank
        String imageName,

        boolean isActive) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
