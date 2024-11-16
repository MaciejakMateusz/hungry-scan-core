package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.ThemeDTO;
import com.hackybear.hungry_scan_core.entity.Theme;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ThemeMapper {

    ThemeDTO toDTO(Theme theme);

    Theme toTheme(ThemeDTO themeDTO);

}
