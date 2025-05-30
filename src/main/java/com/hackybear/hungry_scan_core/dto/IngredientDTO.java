package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngredientDTO(Long id,

                            @AnyTranslationNotBlankDTO
                            @LimitTranslationsLengthDTO
                            @NotNull
                            TranslatableDTO name,

                            BigDecimal price,
                            boolean available,
                            LocalDateTime created,
                            LocalDateTime updated,
                            String modifiedBy,
                            String createdBy) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
