package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IngredientSimpleDTO(
        long id,

        @DefaultTranslationNotBlankDTO
        @LimitTranslationsLengthDTO
        @NotNull
        TranslatableDTO name,

        BigDecimal price,
        boolean available
) {
}
