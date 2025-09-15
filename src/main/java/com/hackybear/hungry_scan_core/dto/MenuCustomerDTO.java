package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record MenuCustomerDTO(List<CategoryCustomerDTO> categories,
                              RestaurantSimpleDTO restaurant,
                              String theme,
                              TranslatableDTO message,
                              boolean bannerIconVisible) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}