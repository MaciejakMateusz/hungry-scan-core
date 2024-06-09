package com.hackybear.hungry_scan_core.test_utils;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.AllergenService;
import com.hackybear.hungry_scan_core.service.interfaces.IngredientService;
import com.hackybear.hungry_scan_core.service.interfaces.LabelService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MenuItemFactory {

    private final IngredientService ingredientService;
    private final AllergenService allergenService;
    private final LabelService labelService;

    public MenuItemFactory(IngredientService ingredientService,
                           AllergenService allergenService,
                           LabelService labelService) {
        this.ingredientService = ingredientService;
        this.allergenService = allergenService;
        this.labelService = labelService;
    }

    public MenuItem createMenuItem(String name,
                                   String description,
                                   Integer categoryId,
                                   BigDecimal price) throws LocalizedException {
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

    private void setIngredients(MenuItem menuItem) throws LocalizedException {
        for (int i = 1; i <= 5; i++) {
            menuItem.addIngredient(ingredientService.findById(i));
        }
    }

    private void setAdditionalIngredients(MenuItem menuItem) throws LocalizedException {
        for (int i = 6; i <= 10; i++) {
            menuItem.addAdditionalIngredient(ingredientService.findById(i));
        }
    }

    private void setLabels(MenuItem menuItem) throws LocalizedException {
        menuItem.addLabel(labelService.findById(3));
        menuItem.addLabel(labelService.findById(5));
    }

    private void setAllergen(MenuItem menuItem) throws LocalizedException {
        menuItem.addAlergen(allergenService.findById(1));
    }

    private Translatable getDefaultTranslation(String translation) {
        Translatable translatable = new Translatable();
        translatable.setDefaultTranslation(translation);
        return translatable;
    }

}
