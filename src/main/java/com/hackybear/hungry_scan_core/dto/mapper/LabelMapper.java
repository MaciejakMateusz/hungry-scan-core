package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.LabelDTO;
import com.hackybear.hungry_scan_core.entity.Label;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TranslatableMapper.class})
public interface LabelMapper {

    LabelDTO toDTO(Label label);

    Label toLabel(LabelDTO labelDTO);

}
