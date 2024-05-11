package pl.rarytas.hungry_scan_core.utility;

import org.springframework.stereotype.Component;
import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.entity.OrderSummary;
import pl.rarytas.hungry_scan_core.entity.OrderedItem;

import java.math.BigDecimal;

@Component
public class OrderServiceHelper {

    public BigDecimal getOrderAmount(Order order) {
        BigDecimal totalAmount = Money.of(0.00);
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            totalAmount = totalAmount.add(orderedItem.getPrice());
        }
        return Money.of(totalAmount);
    }

    public BigDecimal getSummaryAmount(OrderSummary summary) {
        BigDecimal totalAmount = Money.of(0.00);
        for (Order order : summary.getOrders()) {
            totalAmount = totalAmount.add(order.getTotalAmount());
        }
        return Money.of(totalAmount);
    }

    public void prepareForFinalizingDineIn(Order existingOrder) {
        existingOrder.setResolved(true);
    }

    public void prepareForFinalizingTakeAway(Order existingOrder) {
        existingOrder.setResolved(true);
    }

}
