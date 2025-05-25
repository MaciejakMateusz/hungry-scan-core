package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record BannerDTO(
        String id,

        @AnyTranslationNotBlankDTO
        @LimitTranslationsLengthDTO
        @NotNull
        TranslatableDTO name) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
