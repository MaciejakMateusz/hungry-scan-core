package com.hackybear.hungry_scan_core.dto;

import java.io.Serial;
import java.io.Serializable;

public record OrganizationRestaurantDTO(Long id,
                                        String name,
                                        String address,
                                        String postalCode,
                                        String city,
                                        Long organizationId) implements Serializable, Comparable<OrganizationRestaurantDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(OrganizationRestaurantDTO other) {
        return this.id.compareTo(other.id);
    }
}

