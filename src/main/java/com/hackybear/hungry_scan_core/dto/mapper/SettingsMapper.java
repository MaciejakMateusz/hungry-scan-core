package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.entity.Settings;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

    SettingsDTO toDTO(Settings settings);

    Settings toSettings(SettingsDTO settingsDTO);

}
