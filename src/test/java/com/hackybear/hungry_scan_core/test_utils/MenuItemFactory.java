package com.hackybear.hungry_scan_core.test_utils;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.MenuItemVariant;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.AllergenService;
import com.hackybear.hungry_scan_core.service.interfaces.IngredientService;
import com.hackybear.hungry_scan_core.service.interfaces.LabelService;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemVariantService;
import com.hackybear.hungry_scan_core.utility.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MenuItemFactory {

    private final IngredientService ingredientService;
    private final MenuItemVariantService menuItemVariantService;
    private final AllergenService allergenService;
    private final LabelService labelService;

    public MenuItemFactory(IngredientService ingredientService, MenuItemVariantService menuItemVariantService,
                           AllergenService allergenService,
                           LabelService labelService) {
        this.ingredientService = ingredientService;
        this.menuItemVariantService = menuItemVariantService;
        this.allergenService = allergenService;
        this.labelService = labelService;
    }

    public MenuItem createMenuItem(String name,
                                   String description,
                                   BigDecimal price) throws LocalizedException {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setAvailable(true);
        menuItem.setImageName("/public/assets/sample.png");
        setIngredients(menuItem);
        setAdditionalIngredients(menuItem);
        setLabels(menuItem);
        setAllergen(menuItem);
        setVariants(menuItem);
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
        menuItem.addLabel(labelService.findById(9));
    }

    private void setAllergen(MenuItem menuItem) throws LocalizedException {
        menuItem.addAlergen(allergenService.findById(1));
    }

    private void setVariants(MenuItem menuItem) {
        MenuItemVariant variant = new MenuItemVariant();
        variant.setName("Test variant");
        variant.setPrice(Money.of(12.00));
        menuItemVariantService.save(variant);
        menuItem.addVariant(variant);

        MenuItemVariant variant2 = new MenuItemVariant();
        variant2.setName("Test variant 2");
        variant2.setPrice(Money.of(0.00));
        variant2.setDefaultVariant(true);
        menuItemVariantService.save(variant2);
        menuItem.addVariant(variant2);
    }

}
