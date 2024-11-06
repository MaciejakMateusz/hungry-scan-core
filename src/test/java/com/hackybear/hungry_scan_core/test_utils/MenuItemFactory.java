package com.hackybear.hungry_scan_core.test_utils;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.repository.AllergenRepository;
import com.hackybear.hungry_scan_core.repository.IngredientRepository;
import com.hackybear.hungry_scan_core.repository.LabelRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MenuItemFactory {

    private final AllergenRepository allergenRepository;
    private final LabelRepository labelRepository;
    private final IngredientRepository ingredientRepository;

    public MenuItemFactory(AllergenRepository allergenRepository,
                           LabelRepository labelRepository,
                           IngredientRepository ingredientRepository) {
        this.allergenRepository = allergenRepository;
        this.labelRepository = labelRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public MenuItem createMenuItem(String name,
                                   String description,
                                   Long categoryId,
                                   BigDecimal price) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(getDefaultTranslation(name));
        menuItem.setDescription(getDefaultTranslation(description));
        menuItem.setCategoryId(categoryId);
        menuItem.setPrice(price);
        menuItem.setAvailable(true);
        menuItem.setImageName("/public/assets/sample.png");
        menuItem.setDisplayOrder(6);
        setIngredients(menuItem);
        setAdditionalIngredients(menuItem);
        setLabels(menuItem);
        setAllergen(menuItem);
        return menuItem;
    }

    private void setIngredients(MenuItem menuItem) {
        for (long i = 1L; i <= 5; i++) {
            menuItem.addIngredient(ingredientRepository.findById(i).orElseThrow());
        }
    }

    private void setAdditionalIngredients(MenuItem menuItem) {
        for (long i = 6; i <= 10; i++) {
            menuItem.addAdditionalIngredient(ingredientRepository.findById(i).orElseThrow());
        }
    }

    private void setLabels(MenuItem menuItem) {
        menuItem.addLabel(labelRepository.findById(3L).orElseThrow());
        menuItem.addLabel(labelRepository.findById(5L).orElseThrow());
    }

    private void setAllergen(MenuItem menuItem) {
        menuItem.addAlergen(allergenRepository.findById(1L).orElseThrow());
    }

    private Translatable getDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        return translatable;
    }

}
