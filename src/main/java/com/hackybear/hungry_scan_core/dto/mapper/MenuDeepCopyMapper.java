package com.hackybear.hungry_scan_core.dto.mapper;

import com.hackybear.hungry_scan_core.entity.*;
import org.mapstruct.*;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
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
    @Mapping(target = "sourceId", expression = "java(src.getId())")  // carry old id ONLY
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
    default List<Variant> deepCopyVariants(List<Variant> srcVars) {
        return srcVars.stream().map(this::deepCopyVariant).toList();
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
        return new Translatable()
                .withPl(src.getPl())
                .withEn(src.getEn())
                .withFr(src.getFr())
                .withDe(src.getDe())
                .withEs(src.getEs())
                .withUk(src.getUk());
    }

    @AfterMapping
    default void tagSourceIds(@MappingTarget Menu target, Menu src) {
        Iterator<Category> srcCats = src.getCategories().iterator();
        Iterator<Category> dstCats = target.getCategories().iterator();
        while (srcCats.hasNext() && dstCats.hasNext()) {
            Category sc = srcCats.next();
            Category dc = dstCats.next();

            Iterator<MenuItem> srcItems = sc.getMenuItems().iterator();
            Iterator<MenuItem> dstItems = dc.getMenuItems().iterator();
            while (srcItems.hasNext() && dstItems.hasNext()) {
                MenuItem si = srcItems.next();
                MenuItem di = dstItems.next();
                di.setSourceId(si.getId());

                Iterator<Variant> sv = si.getVariants().iterator();
                Iterator<Variant> dv = di.getVariants().iterator();
                while (sv.hasNext() && dv.hasNext()) {
                    dv.next().setMenuItem(di);
                    sv.next();
                }
            }
        }
    }

}