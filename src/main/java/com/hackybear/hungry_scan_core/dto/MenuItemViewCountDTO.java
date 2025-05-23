package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;

public record MenuItemViewCountDTO(Long id,
                                   String pl,
                                   String en,
                                   String fr,
                                   String de,
                                   String es,
                                   String uk,
                                   Integer viewsCount) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
