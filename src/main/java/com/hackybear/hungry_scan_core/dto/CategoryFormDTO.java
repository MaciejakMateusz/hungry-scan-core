package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.NotNull;

public record CategoryFormDTO(
        Long id,

        @DefaultTranslationNotBlankDTO
        @LimitTranslationsLengthDTO
        @NotNull
        TranslatableDTO name,

        boolean available,

        @NotNull
        Integer displayOrder) {
}