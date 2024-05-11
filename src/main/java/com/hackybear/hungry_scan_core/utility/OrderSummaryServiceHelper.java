package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import org.springframework.stereotype.Component;

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
