package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MenuDeepCopyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "standard", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "categories", qualifiedByName = "deepCopyCategories")
    @Mapping(target = "message", qualifiedByName = "deepCopyTranslatable")
    Menu duplicateMenu(Menu source);

    @Named("deepCopyCategories")
    default Set<Category> deepCopyCategories(Set<Category> srcCats) {
        return srcCats.stream()
                .map(this::deepCopyCategory)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menu", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "menuItems", qualifiedByName = "deepCopyItems")
    @Mapping(target = "name", qualifiedByName = "deepCopyTranslatable")
    Category deepCopyCategory(Category src);

    @Named("deepCopyItems")
    default Set<MenuItem> deepCopyItems(Set<MenuItem> srcItems) {
        return srcItems.stream()
                .map(this::deepCopyItem)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "name", qualifiedByName = "deepCopyTranslatable")
    @Mapping(target = "description", qualifiedByName = "deepCopyTranslatable")
    @Mapping(target = "variants", qualifiedByName = "deepCopyVariants")
    MenuItem deepCopyItem(MenuItem src);

    @Named("deepCopyVariants")
    default Set<Variant> deepCopyVariants(Set<Variant> srcVars) {
        return srcVars.stream()
                .map(this::deepCopyVariant)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "menuItem", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "name", qualifiedByName = "deepCopyTranslatable")
    Variant deepCopyVariant(Variant src);

    @Named("deepCopyTranslatable")
    default Translatable deepCopyTranslatable(Translatable src) {
        Translatable copy = new Translatable();
        copy.setDefaultTranslation(src.getDefaultTranslation());
        copy.setTranslationEn(src.getTranslationEn());
        return copy;
    }

}