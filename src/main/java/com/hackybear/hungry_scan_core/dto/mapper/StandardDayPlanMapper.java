package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.StandardDayPlanDTO;
import com.hackybear.hungry_scan_core.entity.StandardDayPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DayTimeRangeMapper.class})
public interface StandardDayPlanMapper {

    @Mapping(expression = "java(standardDayPlan.getMenu().getId())",
            target = "menuId")
    StandardDayPlanDTO toDTO(StandardDayPlan standardDayPlan);

    @Mapping(expression = "java(new com.hackybear.hungry_scan_core.entity.Menu(standardDayPlan.menuId()))",
            target = "menu")
    StandardDayPlan toStandardDayPlan(StandardDayPlanDTO standardDayPlan);

}
