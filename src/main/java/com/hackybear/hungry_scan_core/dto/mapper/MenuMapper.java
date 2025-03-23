package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.MenuDTO;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.entity.Menu;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ScheduleMapper.class})
public interface MenuMapper {

    MenuSimpleDTO toSimpleDTO(Menu menu);

    MenuDTO toDTO(Menu menu);

    Menu toMenu(MenuSimpleDTO menuSimpleDTO);

    Menu toMenu(MenuDTO menuDTO);

}
