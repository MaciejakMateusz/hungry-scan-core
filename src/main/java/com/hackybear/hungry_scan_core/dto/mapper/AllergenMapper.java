package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.AllergenDTO;
import com.hackybear.hungry_scan_core.entity.Allergen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TranslatableMapper.class})
public interface AllergenMapper {

    AllergenDTO toDTO(Allergen allergen);

    Allergen toAllergen(AllergenDTO allergenDTO);

}
