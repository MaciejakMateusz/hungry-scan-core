package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.DayTimeRangeDTO;
import com.hackybear.hungry_scan_core.entity.DayTimeRange;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DayTimeRangeMapper {

    @Mapping(expression = "java(dayTimeRange.getStandardDayPlan().getId())",
            target = "standardDayPlanId")
    DayTimeRangeDTO toDTO(DayTimeRange dayTimeRange);

    @Mapping(expression = "java(new com.hackybear.hungry_scan_core.entity.StandardDayPlan(dayTimeRangeDTO.standardDayPlanId()))",
            target = "standardDayPlan")
    DayTimeRange toDayTimeRange(DayTimeRangeDTO dayTimeRangeDTO);

}
