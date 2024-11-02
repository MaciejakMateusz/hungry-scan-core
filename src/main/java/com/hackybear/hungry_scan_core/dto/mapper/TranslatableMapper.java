package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.TranslatableDTO;
import com.hackybear.hungry_scan_core.entity.Translatable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TranslatableMapper {

    TranslatableDTO toDTO(Translatable translatable);

    Translatable toTranslatable(TranslatableDTO translatableDTO);

}
