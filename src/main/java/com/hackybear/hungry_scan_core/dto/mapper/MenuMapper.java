package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.MenuCustomerDTO;
import com.hackybear.hungry_scan_core.dto.MenuDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StandardDayPlanMapper.class, TranslatableMapper.class})
public interface MenuMapper {

    @Mapping(expression = "java(menu.getRestaurant().getId())",
            target = "restaurantId")
    MenuSimpleDTO toSimpleDTO(Menu menu);

    @Mapping(expression = "java(new com.hackybear.hungry_scan_core.entity.Restaurant(menuSimpleDTO.restaurantId()))",
            target = "restaurant")
    Menu toMenu(MenuSimpleDTO menuSimpleDTO);

    MenuDTO toDTO(Menu menu);

    Menu toMenu(MenuDTO menuDTO);

    @Mapping(expression = "java(menu.getTheme().getHex())", target = "theme")
    MenuCustomerDTO toCustomerDTO(Menu menu);

}
