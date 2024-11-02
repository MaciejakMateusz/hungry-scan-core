package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.VariantDTO;
import com.hackybear.hungry_scan_core.entity.Variant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TranslatableMapper.class})
public interface VariantMapper {

    VariantDTO toDTO(Variant variant);

    Variant toVariant(VariantDTO variantDTO);

}
