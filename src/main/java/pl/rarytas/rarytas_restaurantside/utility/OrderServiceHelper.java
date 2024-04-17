package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Ingredient;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;

import java.math.BigDecimal;

@Component
public class OrderServiceHelper {

    public BigDecimal calculateTotalAmount(Order order) {
        BigDecimal sum = Money.of(0.00);
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            BigDecimal itemPrice = orderedItem.getMenuItemVariant().getPrice();
            BigDecimal totalItemPrice = computeTotalItemPrice(orderedItem, itemPrice);
            sum = sum.add(totalItemPrice);
        }
        return Money.of(sum);
    }

    private BigDecimal computeTotalItemPrice(OrderedItem orderedItem, BigDecimal itemPrice) {
        int quantity = orderedItem.getQuantity();
        BigDecimal totalItemPrice = itemPrice.multiply(BigDecimal.valueOf(quantity));
        for (Ingredient additionalIngredient : orderedItem.getAdditionalIngredients()) {
            BigDecimal ingredientPrice = additionalIngredient.getPrice();
            totalItemPrice = totalItemPrice.add(ingredientPrice);
        }
        return totalItemPrice;
    }

    public void prepareForFinalizingDineIn(Order existingOrder) {
        existingOrder.setResolved(true);
    }

    public void prepareForFinalizingTakeAway(Order existingOrder) {
        existingOrder.setResolved(true);
    }

}
