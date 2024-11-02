package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.IngredientDTO;
import com.hackybear.hungry_scan_core.dto.IngredientSimpleDTO;
import com.hackybear.hungry_scan_core.entity.Ingredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TranslatableMapper.class})
public interface IngredientMapper {

    IngredientDTO toDTO(Ingredient ingredient);

    Ingredient toIngredient(IngredientDTO ingredientDTO);

    IngredientSimpleDTO toSimpleDTO(Ingredient ingredient);

    Ingredient toIngredient(IngredientSimpleDTO ingredientSimpleDTO);

}
