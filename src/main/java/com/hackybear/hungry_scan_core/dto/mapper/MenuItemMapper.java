package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemVariantsDTO;
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
    @Mapping(expression = "java(menuItem.getAdditionalIngredients().size())",
            target = "additionsCount")
    MenuItemSimpleDTO toDTO(MenuItem menuItem);

    MenuItem toMenuItem(MenuItemSimpleDTO menuItemSimpleDTO);

    @Mapping(expression = "java(menuItem.getCategory().getId())",
            target = "categoryId")
    MenuItemFormDTO toFormDTO(MenuItem menuItem);

    @Mapping(expression = "java(new Category(menuItemFormDTO.categoryId()))",
            target = "category")
    MenuItem toMenuItem(MenuItemFormDTO menuItemFormDTO);

    MenuItemVariantsDTO toVariantsDTO(MenuItem menuItem);

}