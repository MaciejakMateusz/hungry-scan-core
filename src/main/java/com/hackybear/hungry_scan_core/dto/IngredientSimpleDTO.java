package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record IngredientSimpleDTO(
        Long id,

        @AnyTranslationNotBlankDTO
        @LimitTranslationsLengthDTO
        @NotNull
        TranslatableDTO name,

        BigDecimal price,
        boolean available) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

}
