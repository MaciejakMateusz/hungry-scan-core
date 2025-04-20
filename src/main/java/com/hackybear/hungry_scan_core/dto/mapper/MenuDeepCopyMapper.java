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
    @Mapping(target = "categories", qualifiedByName = "deepCopyCategories")
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
    @Mapping(target = "labels", qualifiedByName = "deepCopyLabels")
    @Mapping(target = "banners", qualifiedByName = "deepCopyBanners")
    @Mapping(target = "allergens", qualifiedByName = "deepCopyAllergens")
    @Mapping(target = "ingredients", qualifiedByName = "deepCopyIngredients")
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

    @Named("deepCopyLabels")
    default Set<Label> deepCopyLabels(Set<Label> srcLabels) {
        return srcLabels.stream()
                .map(l -> {
                    Label copy = new Label();
                    copy.setName(l.getName());
                    copy.setRestaurantId(l.getRestaurantId());
                    copy.setIconName(l.getIconName());
                    return copy;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Named("deepCopyBanners")
    default Set<Banner> deepCopyBanners(Set<Banner> srcBanner) {
        return srcBanner.stream()
                .map(b -> {
                    Banner copy = new Banner();
                    copy.setName(b.getName());
                    return copy;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Named("deepCopyAllergens")
    default Set<Allergen> deepCopyAllergens(Set<Allergen> srcAllergens) {
        return srcAllergens.stream()
                .map(a -> {
                    Allergen copy = new Allergen();
                    copy.setName(a.getName());
                    copy.setDescription(a.getDescription());
                    copy.setIconName(a.getIconName());
                    return copy;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Named("deepCopyIngredients")
    default Set<Ingredient> deepCopyIngredients(Set<Ingredient> srcIngredients) {
        return srcIngredients.stream()
                .map(i -> {
                    Ingredient copy = new Ingredient();
                    copy.setRestaurantId(i.getRestaurantId());
                    copy.setName(i.getName());
                    copy.setPrice(i.getPrice());
                    copy.setAvailable(i.isAvailable());
                    return copy;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Named("deepCopyTranslatable")
    default Translatable deepCopyTranslatable(Translatable src) {
        Translatable copy = new Translatable();
        copy.setDefaultTranslation(src.getDefaultTranslation());
        copy.setTranslationEn(src.getTranslationEn());
        return copy;
    }

}