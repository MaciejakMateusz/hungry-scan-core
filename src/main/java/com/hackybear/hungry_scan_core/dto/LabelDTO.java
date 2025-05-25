package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record LabelDTO(
        long id,

        @AnyTranslationNotBlankDTO
        @LimitTranslationsLengthDTO
        @NotNull
        TranslatableDTO name,

        String iconName) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
