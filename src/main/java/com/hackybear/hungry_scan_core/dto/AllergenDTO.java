package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlankDTO;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLengthDTO;
import jakarta.validation.constraints.NotNull;

public record AllergenDTO(long id,

                          @DefaultTranslationNotBlankDTO
                          @LimitTranslationsLengthDTO
                          @NotNull
                          TranslatableDTO name,

                          @DefaultTranslationNotBlankDTO
                          @LimitTranslationsLengthDTO
                          @NotNull
                          TranslatableDTO description,
                          String iconName) {
}