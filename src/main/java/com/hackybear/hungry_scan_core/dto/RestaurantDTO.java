package com.hackybear.hungry_scan_core.dto;

import com.hackybear.hungry_scan_core.annotation.AtLeastOneDayOpenDTO;
import com.hackybear.hungry_scan_core.annotation.OpeningClosingTimeDTO;
import com.hackybear.hungry_scan_core.annotation.SupportedLanguagesNotEmptyDTO;
import com.hackybear.hungry_scan_core.entity.PricePlan;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public record RestaurantDTO(
        Long id,

        @Length(max = 36)
        String token,

        Integer qrVersion,

        @NotBlank
        String name,

        @NotBlank
        String address,

        @NotBlank
        String postalCode,

        @NotBlank
        String city,

        Set<MenuSimpleDTO> menus,

        @AtLeastOneDayOpenDTO
        @OpeningClosingTimeDTO
        @SupportedLanguagesNotEmptyDTO
        SettingsDTO settings,

        PricePlan pricePlan,

        Instant pricePlanTo,

        Instant created,

        LocalDateTime updated,

        String createdBy,

        String modifiedBy) implements Serializable, Comparable<RestaurantDTO> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(RestaurantDTO other) {
        return this.name.compareTo(other.name);
    }
}
