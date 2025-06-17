package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.MenuColorDTO;
import com.hackybear.hungry_scan_core.entity.MenuColor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuColorMapper {

    MenuColorDTO toDTO(MenuColor menuColor);

    MenuColor toMenuColor(MenuColorDTO menuColorDTO);

}
