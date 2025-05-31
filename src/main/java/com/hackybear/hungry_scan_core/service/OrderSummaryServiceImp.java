package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.repository.OrderSummaryRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantTableRepository;
import com.hackybear.hungry_scan_core.service.interfaces.OrderSummaryService;
import com.hackybear.hungry_scan_core.utility.Money;
import com.hackybear.hungry_scan_core.utility.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderSummaryServiceImp implements OrderSummaryService {

    private final OrderSummaryRepository orderSummaryRepository;
    private final PaymentProcessor paymentProcessor;
    private final RestaurantTableRepository restaurantTableRepository;

    @Transactional
    @Override
    public OrderSummary pay(OrderSummary orderSummary) {
        prepareSummary(orderSummary);
        return paymentProcessor.pay(orderSummary);
    }

    @Override
    public void delete(OrderSummary orderSummary) {
        orderSummaryRepository.delete(orderSummary);
    }

    private void prepareSummary(OrderSummary orderSummary) {
        orderSummary.getRestaurantTable().setBillRequested(true);
        restaurantTableRepository.save(orderSummary.getRestaurantTable());
        orderSummary.setTotalAmount(calculateTotalPrice(orderSummary));
    }

    private BigDecimal calculateTotalPrice(OrderSummary orderSummary) {
        BigDecimal totalPrice = Money.of(0.00);
        for (Order order : orderSummary.getOrders()) {
            totalPrice = totalPrice.add(order.getTotalAmount());
        }
        totalPrice = totalPrice.add(orderSummary.getTipAmount());
        return Money.of(totalPrice);
    }

}