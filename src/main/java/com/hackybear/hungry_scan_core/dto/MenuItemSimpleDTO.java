package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MenuItemSimpleDTO(Long id,
                                String imageName,
                                TranslatableDTO name,
                                TranslatableDTO description,
                                Long categoryId,
                                BigDecimal price,
                                int labelsCount,
                                int allergensCount,
                                int variantsCount,
                                Integer displayOrder,
                                boolean available,
                                boolean visible,
                                boolean isNew,
                                boolean isBestseller,
                                LocalDateTime created,
                                LocalDateTime updated,
                                String modifiedBy,
                                String createdBy) implements Serializable, Comparable<MenuItemSimpleDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(MenuItemSimpleDTO o) {
        return this.displayOrder.compareTo(o.displayOrder);
    }
}
