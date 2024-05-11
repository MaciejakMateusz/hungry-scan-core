package pl.rarytas.hungry_scan_core.utility;

import org.springframework.stereotype.Component;
import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.entity.OrderSummary;

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
