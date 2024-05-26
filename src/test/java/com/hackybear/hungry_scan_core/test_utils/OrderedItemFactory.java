package com.hackybear.hungry_scan_core.test_utils;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.entity.OrderedItem;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import com.hackybear.hungry_scan_core.utility.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderedItemFactory {

    private final MenuItemService menuItemService;
    private final VariantRepository variantRepository;

    public OrderedItemFactory(MenuItemService menuItemService, VariantRepository variantRepository) {
        this.menuItemService = menuItemService;
        this.variantRepository = variantRepository;
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
        List<Variant> variants = variantRepository.findAllByMenuItemIdOrderByDisplayOrder(menuItem.getId());
        if (!variants.isEmpty()) {
            Variant variant =
                    variants.stream()
                            .filter(v -> Objects.equals(v.getId(), variantId))
                            .findFirst()
                            .orElseThrow();
            orderedItem.setVariant(variant);
        }
    }

    private void setAdditionalIngredients(OrderedItem orderedItem, String... chosenIngredients) {
        MenuItem menuItem = orderedItem.getMenuItem();
        List<String> orderedIngredients = Arrays.asList(chosenIngredients);
        Set<Ingredient> availableIngredients = menuItem.getAdditionalIngredients();
        availableIngredients = availableIngredients.stream()
                .filter(ingredient -> orderedIngredients.stream()
                        .anyMatch(ingredientName -> ingredientName.equals(ingredient.getName().getDefaultTranslation())))
                .collect(Collectors.toSet());
        orderedItem.setAdditionalIngredients(availableIngredients);
    }

    private BigDecimal computePrice(OrderedItem orderedItem) {
        BigDecimal price = Money.of(0.00);
        BigDecimal menuItemPrice = orderedItem.getMenuItem().getPrice();
        Variant variant = orderedItem.getVariant();
        BigDecimal variantModifier = Objects.nonNull(variant) ? variant.getPrice() : Money.of(0.00);
        price = price.add(menuItemPrice);
        price = price.add(variantModifier);
        for (Ingredient ingredient : orderedItem.getAdditionalIngredients()) {
            price = price.add(ingredient.getPrice());
        }
        price = price.multiply(BigDecimal.valueOf(orderedItem.getQuantity()));
        return Money.of(price);
    }

}