package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.MenuCustomerDTO;
import com.hackybear.hungry_scan_core.dto.MenuDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.enums.Theme;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TranslatableMapper.class, MenuPlanMapper.class})
public interface MenuMapper {

    @Named("themeToHex")
    default String themeToHex(Theme theme) {
        return theme == null ? null : theme.getHex();
    }

    @Named("hexToTheme")
    default Theme hexToTheme(String hex) {
        if (hex == null) return null;
        Theme theme = Theme.HEX_MAP.get(hex.toUpperCase());
        if (theme == null) {
            throw new IllegalArgumentException("Unknown theme hex: " + hex);
        }
        return theme;
    }

    @Mapping(expression = "java(menu.getRestaurant().getId())",
            target = "restaurantId")
    @Mapping(qualifiedByName = "themeToHex", target = "theme")
    MenuSimpleDTO toSimpleDTO(Menu menu);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "color", ignore = true)
    @Mapping(qualifiedByName = "hexToTheme", target = "theme")
    void updateFromSimpleDTO(MenuSimpleDTO menuSimpleDTO, @MappingTarget Menu menu);

    @Mapping(expression = "java(new com.hackybear.hungry_scan_core.entity.Restaurant(menuSimpleDTO.restaurantId()))",
            target = "restaurant")
    @Mapping(qualifiedByName = "hexToTheme", target = "theme")
    Menu toMenu(MenuSimpleDTO menuSimpleDTO);

    @Mapping(qualifiedByName = "themeToHex", target = "theme")
    MenuDTO toDTO(Menu menu);

    Menu toMenu(MenuDTO menuDTO);

    @Mapping(qualifiedByName = "themeToHex", target = "theme")
    MenuCustomerDTO toCustomerDTO(Menu menu);

}
