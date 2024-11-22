package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.ScheduleDTO;
import com.hackybear.hungry_scan_core.entity.Schedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduleDTO toDTO(Schedule schedule);

    Schedule toSchedule(ScheduleDTO scheduleDTO);

}
