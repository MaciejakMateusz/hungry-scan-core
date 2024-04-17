package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderSummary;

import java.math.BigDecimal;

@Component
public class OrderSummaryServiceHelper {

    public BigDecimal calculateTotalPrice(OrderSummary orderSummary) {
        BigDecimal totalPrice = Money.of(0.00);
        for (Order order : orderSummary.getOrders()) {
            totalPrice = totalPrice.add(order.getTotalAmount());
        }
        totalPrice = totalPrice.add(orderSummary.getTipAmount());
        return Money.of(totalPrice);
    }
}
