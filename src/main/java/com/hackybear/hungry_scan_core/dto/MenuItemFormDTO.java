package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;

public record MenuItemFormDTO(Long id,
                              String imageName,

                              @DefaultTranslationNotBlankDTO
                              @LimitTranslationsLengthDTO
                              @NotNull
                              TranslatableDTO name,

                              @LimitTranslationsLengthDTO
                              TranslatableDTO description,

                              @NotNull
                              Long categoryId,

                              @DecimalMin(value = "1", message = "Cena musi być większa od 1zł")
                              @NotNull
                              BigDecimal price,

                              Set<LabelDTO> labels,
                              Set<AllergenDTO> allergens,
                              Set<IngredientSimpleDTO> additionalIngredients,
                              Set<VariantDTO> variants,

                              boolean available,
                              boolean visible,
                              boolean isNew,
                              boolean isBestseller) {
}
