package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VariantDTO(Long id,

                         @DefaultTranslationNotBlankDTO
                         @LimitTranslationsLengthDTO
                         @NotNull
                         TranslatableDTO name,

                         @NotNull
                         Long menuItemId,

                         @DecimalMin(value = "0.00")
                         BigDecimal price,
                         boolean available,
                         boolean defaultVariant,

                         Integer displayOrder) {
}
