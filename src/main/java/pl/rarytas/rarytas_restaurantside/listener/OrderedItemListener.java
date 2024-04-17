package pl.rarytas.rarytas_restaurantside.listener;

import jakarta.persistence.PostRemove;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.entity.Ingredient;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.math.BigDecimal;

@Slf4j
@Getter
@Setter
public class OrderedItemListener {

    @PrePersist
    protected void prePersist(final OrderedItem orderedItem) {
        BigDecimal price = Money.of(0.00);
        price = price.add(orderedItem.getMenuItem().getPrice());
        price = price.add(orderedItem.getMenuItemVariant().getPrice());
        for (Ingredient ingredient : orderedItem.getAdditionalIngredients()) {
            price = price.add(ingredient.getPrice());
        }
        price = price.multiply(BigDecimal.valueOf(orderedItem.getQuantity()));
        orderedItem.setPrice(Money.of(price));
    }

    @PostRemove
    public void postRemove(final OrderedItem orderedItem) {
        log.info("Removed ordered item with ID: {}", orderedItem.getId());
    }

}