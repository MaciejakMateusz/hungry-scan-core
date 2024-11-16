package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.StatisticsDTO;
import com.hackybear.hungry_scan_core.entity.Statistics;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {

    StatisticsDTO toDTO(Statistics statistics);

    Statistics toStatistics(StatisticsDTO statisticsDTO);

}
