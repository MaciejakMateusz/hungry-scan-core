package com.hackybear.hungry_scan_core.test_utils;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuItemMapper;
import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.OrderedItem;
import com.hackybear.hungry_scan_core.entity.Variant;
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
    private final MenuItemMapper menuItemMapper;

    public OrderedItemFactory(MenuItemService menuItemService, VariantRepository variantRepository, MenuItemMapper menuItemMapper) {
        this.menuItemService = menuItemService;
        this.variantRepository = variantRepository;
        this.menuItemMapper = menuItemMapper;
    }

    public OrderedItem createOrderedItem(Long menuItemId,
                                         Long variantId,
                                         String comment,
                                         Integer quantity,
                                         String... chosenIngredients) throws LocalizedException {
        OrderedItem orderedItem = new OrderedItem();
        MenuItemFormDTO menuItemFormDTO = menuItemService.findById(menuItemId);
        MenuItem menuItem = menuItemMapper.toMenuItem(menuItemFormDTO);
        orderedItem.setMenuItem(menuItem);
        setVariant(orderedItem, variantId);
        orderedItem.setAdditionalComment(comment);
        setAdditionalIngredients(orderedItem, chosenIngredients);
        orderedItem.setQuantity(quantity);
        orderedItem.setPrice(computePrice(orderedItem));
        return orderedItem;
    }

    private void setVariant(OrderedItem orderedItem, Long variantId) {
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
                        .anyMatch(ingredientName -> ingredientName.equals(ingredient.getName().getPl())))
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