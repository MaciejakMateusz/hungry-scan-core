package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.entity.Settings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

    @Mapping(expression = "java(settings.getRestaurant() != null ? settings.getRestaurant().getId() : null)",
            target = "restaurantId")
    SettingsDTO toDTO(Settings settings);

    @Mapping(expression = "java(settingsDTO.restaurantId() != null ? new com.hackybear.hungry_scan_core.entity.Restaurant(settingsDTO.restaurantId()) : null)",
            target = "restaurant")
    Settings toSettings(SettingsDTO settingsDTO);

}
