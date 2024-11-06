package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngredientDTO(long id,

                            @DefaultTranslationNotBlankDTO
                            @LimitTranslationsLengthDTO
                            @NotNull
                            TranslatableDTO name,

                            BigDecimal price,
                            boolean available,
                            LocalDateTime created,
                            LocalDateTime updated,
                            String modifiedBy,
                            String createdBy) implements Comparable<IngredientDTO> {

    @Override
    public int compareTo(IngredientDTO other) {
        return this.name.defaultTranslation().compareTo(other.name().defaultTranslation());
    }
}
