package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;

public record MenuItemViewCountDTO(Long id,
                                   String defaultTranslation,
                                   String translationEn,
                                   Integer viewsCount) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
