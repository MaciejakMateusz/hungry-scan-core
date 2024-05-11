package pl.rarytas.hungry_scan_core.test_utils;

import org.springframework.stereotype.Component;
import pl.rarytas.hungry_scan_core.entity.Ingredient;
import pl.rarytas.hungry_scan_core.entity.MenuItem;
import pl.rarytas.hungry_scan_core.entity.MenuItemVariant;
import pl.rarytas.hungry_scan_core.entity.OrderedItem;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.service.interfaces.MenuItemService;
import pl.rarytas.hungry_scan_core.utility.Money;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderedItemFactory {

    private final MenuItemService menuItemService;

    public OrderedItemFactory(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    public OrderedItem createOrderedItem(Integer menuItemId,
                                         Integer variantId,
                                         String comment,
                                         Integer quantity,
                                         String... chosenIngredients) throws LocalizedException {
        OrderedItem orderedItem = new OrderedItem();
        MenuItem menuItem = menuItemService.findById(menuItemId);
        orderedItem.setMenuItem(menuItem);
        setVariant(orderedItem, variantId);
        orderedItem.setAdditionalComment(comment);
        setAdditionalIngredients(orderedItem, chosenIngredients);
        orderedItem.setQuantity(quantity);
        orderedItem.setPrice(computePrice(orderedItem));
        return orderedItem;
    }

    private void setVariant(OrderedItem orderedItem, Integer variantId) {
        MenuItem menuItem = orderedItem.getMenuItem();
        Set<MenuItemVariant> variants = menuItem.getVariants();
        if (!variants.isEmpty()) {
            MenuItemVariant menuItemVariant =
                    variants.stream()
                            .filter(variant -> Objects.equals(variant.getId(), variantId))
                            .findFirst()
                            .orElseThrow();
            orderedItem.setMenuItemVariant(menuItemVariant);
        }
    }

    private void setAdditionalIngredients(OrderedItem orderedItem, String... chosenIngredients) {
        MenuItem menuItem = orderedItem.getMenuItem();
        List<String> orderedIngredients = Arrays.asList(chosenIngredients); // Convert array to list
        Set<Ingredient> availableIngredients = menuItem.getAdditionalIngredients();
        availableIngredients = availableIngredients.stream()
                .filter(ingredient -> orderedIngredients.stream()
                        .anyMatch(ingredientName -> ingredientName.equals(ingredient.getName())))
                .collect(Collectors.toSet());
        orderedItem.setAdditionalIngredients(availableIngredients);
    }

    private BigDecimal computePrice(OrderedItem orderedItem) {
        BigDecimal price = Money.of(0.00);
        BigDecimal menuItemPrice = orderedItem.getMenuItem().getPrice();
        BigDecimal variantModifier = orderedItem.getMenuItemVariant().getPrice();
        price = price.add(menuItemPrice);
        price = price.add(variantModifier);
        for (Ingredient ingredient : orderedItem.getAdditionalIngredients()) {
            price = price.add(ingredient.getPrice());
        }
        price = price.multiply(BigDecimal.valueOf(orderedItem.getQuantity()));
        return Money.of(price);
    }

}