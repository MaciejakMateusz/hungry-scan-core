package com.hackybear.hungry_scan_core.listener;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.entity.OrderedItem;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Getter
@Setter
public class OrderedItemListener {

    @PrePersist
    protected void prePersist(final OrderedItem orderedItem) {
        BigDecimal price = Money.of(0.00);
        price = price.add(orderedItem.getMenuItem().getPrice());
        Variant variant = orderedItem.getVariant();
        BigDecimal variantModifier = Objects.nonNull(variant) ? variant.getPrice() : Money.of(0.00);
        price = price.add(variantModifier);
        for (Ingredient ingredient : orderedItem.getAdditionalIngredients()) {
            price = price.add(ingredient.getPrice());
        }
        price = price.multiply(BigDecimal.valueOf(orderedItem.getQuantity()));
        orderedItem.setPrice(Money.of(price));
    }

}