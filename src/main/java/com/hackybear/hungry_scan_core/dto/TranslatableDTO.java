package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;

public record TranslatableDTO(Long id,
                              String defaultTranslation,
                              String translationEn) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
