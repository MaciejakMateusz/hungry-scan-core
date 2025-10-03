package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record MenuItemVariantsDTO(Long id,
                                  TranslatableDTO name,
                                  List<VariantDTO> variants,
                                  Integer displayOrder) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
