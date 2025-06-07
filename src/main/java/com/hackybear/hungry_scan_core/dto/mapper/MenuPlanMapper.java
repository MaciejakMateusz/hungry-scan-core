package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.MenuPlanDTO;
import com.hackybear.hungry_scan_core.entity.MenuPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuPlanMapper {

    @Mapping(expression = "java(menuPlan.getMenu().getId())",
            target = "menuId")
    MenuPlanDTO toDTO(MenuPlan menuPlan);

    @Mapping(expression = "java(new com.hackybear.hungry_scan_core.entity.Menu(menuPlanDTO.menuId()))",
            target = "menu")
    MenuPlan toMenuPlan(MenuPlanDTO menuPlanDTO);

}
