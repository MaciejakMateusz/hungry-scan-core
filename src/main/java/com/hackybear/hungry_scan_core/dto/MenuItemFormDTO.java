package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import com.hackybear.hungry_scan_core.entity.Banner;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MenuItemFormDTO(Long id,

                              @AnyTranslationNotBlankDTO
                              @LimitTranslationsLengthDTO
                              @NotNull
                              TranslatableDTO name,

                              @LimitTranslationsLengthDTO
                              TranslatableDTO description,

                              @NotNull
                              Long categoryId,

                              @DecimalMin(value = "1", message = "{jakarta.validation.constraints.MinPrice.message}")
                              @NotNull
                              BigDecimal price,

                              @DecimalMin(value = "1", message = "{jakarta.validation.constraints.MinPrice.message}")
                              BigDecimal promoPrice,

                              Set<LabelDTO> labels,
                              Set<AllergenDTO> allergens,
                              Set<IngredientSimpleDTO> additionalIngredients,
                              List<VariantDTO> variants,
                              Set<Banner> banners,
                              Integer displayOrder,
                              boolean available,
                              LocalDateTime updated) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
