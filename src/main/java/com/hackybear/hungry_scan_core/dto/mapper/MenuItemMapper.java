package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        TranslatableMapper.class,
        LabelMapper.class,
        AllergenMapper.class,
        IngredientMapper.class
})
public interface MenuItemMapper {

    @Mapping(expression = "java(menuItem.getLabels().size())",
            target = "labelsCount")
    @Mapping(expression = "java(menuItem.getAllergens().size())",
            target = "allergensCount")
    @Mapping(expression = "java(menuItem.getVariants().size())",
            target = "variantsCount")
    MenuItemSimpleDTO toDTO(MenuItem menuItem);

    MenuItem toMenuItem(MenuItemSimpleDTO menuItemSimpleDTO);

    MenuItemFormDTO toFormDTO(MenuItem menuItem);

    MenuItem toMenuItem(MenuItemFormDTO menuItemFormDTO);


}
