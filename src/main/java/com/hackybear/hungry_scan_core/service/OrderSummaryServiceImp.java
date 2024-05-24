package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.OrderSummaryRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantTableRepository;
import com.hackybear.hungry_scan_core.service.interfaces.OrderSummaryService;
import com.hackybear.hungry_scan_core.utility.Money;
import com.hackybear.hungry_scan_core.utility.PaymentProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class OrderSummaryServiceImp implements OrderSummaryService {

    private final OrderSummaryRepository orderSummaryRepository;
    private final ExceptionHelper exceptionHelper;
    private final PaymentProcessor paymentProcessor;
    private final RestaurantTableRepository restaurantTableRepository;

    public OrderSummaryServiceImp(OrderSummaryRepository orderSummaryRepository,
                                  ExceptionHelper exceptionHelper, PaymentProcessor paymentProcessor, RestaurantTableRepository restaurantTableRepository) {
        this.orderSummaryRepository = orderSummaryRepository;
        this.exceptionHelper = exceptionHelper;
        this.paymentProcessor = paymentProcessor;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public OrderSummary findByTableNumber(Integer tableNumber) throws LocalizedException {
        return orderSummaryRepository.findFirstByRestaurantTableId(tableNumber)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.orderNotFoundByTable", tableNumber));
    }

    @Transactional
    @Override
    public OrderSummary pay(OrderSummary orderSummary) {
        prepareSummary(orderSummary);
        return paymentProcessor.pay(orderSummary);
//        dataTransferServiceImpl.archiveSummary(orderSummary);
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