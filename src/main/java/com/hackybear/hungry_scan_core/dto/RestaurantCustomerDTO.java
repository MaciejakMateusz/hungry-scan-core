package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.enums.Language;

import java.io.Serial;
import java.io.Serializable;

public record RestaurantCustomerDTO(Long id,
                                    String name,
                                    Language language) implements Serializable, Comparable<RestaurantCustomerDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(RestaurantCustomerDTO other) {
        return this.name.compareTo(other.name);
    }
}

