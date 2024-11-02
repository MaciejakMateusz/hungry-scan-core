package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.dto.CategoryCustomerDTO;
import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TranslatableMapper.class, MenuItemMapper.class})
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    Category toCategory(CategoryDTO categoryDTO);

    CategoryFormDTO toFormDTO(Category category);

    Category toCategory(CategoryFormDTO categoryFormDTO);

    CategoryCustomerDTO toCustomerDTO(Category category);

    Category toCategory(CategoryCustomerDTO categoryCustomerDTO);

}