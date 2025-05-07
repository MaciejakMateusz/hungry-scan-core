package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import com.hackybear.hungry_scan_core.entity.Banner;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

public record MenuItemFormDTO(Long id,

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
                              BigDecimal promoPrice,

                              Set<LabelDTO> labels,
                              Set<AllergenDTO> allergens,
                              Set<IngredientSimpleDTO> additionalIngredients,
                              Set<VariantDTO> variants,
                              Set<Banner> banners,

                              Integer displayOrder,

                              boolean available) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
