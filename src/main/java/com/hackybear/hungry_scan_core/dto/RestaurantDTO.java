package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.entity.PricePlan;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

public record RestaurantDTO(
        Long id,

        @Length(max = 36)
        String token,

        @NotBlank
        String name,

        @NotBlank
        String address,

        @NotBlank
        String postalCode,

        @NotBlank
        String city,

        Set<MenuSimpleDTO> menus,

        SettingsDTO settings,

        PricePlan pricePlan,

        Instant pricePlanTo,

        Instant created) implements Serializable, Comparable<RestaurantDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(RestaurantDTO other) {
        return this.name.compareTo(other.name);
    }
}
