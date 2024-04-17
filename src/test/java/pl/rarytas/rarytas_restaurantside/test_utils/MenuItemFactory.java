package pl.rarytas.rarytas_restaurantside.test_utils;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.AllergenService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.IngredientService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.LabelService;

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

}
