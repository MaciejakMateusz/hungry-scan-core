package com.hackybear.hungry_scan_core.test_utils;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderedItem;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import com.hackybear.hungry_scan_core.utility.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class OrderFactory {
    private final RestaurantService restaurantService;
    private final RestaurantTableService restaurantTableService;

    public OrderFactory(RestaurantService restaurantService, RestaurantTableService restaurantTableService) {
        this.restaurantService = restaurantService;
        this.restaurantTableService = restaurantTableService;
    }

    public Order createOrder(Integer tableId, boolean isForTakeAway, OrderedItem... chosenItems) throws LocalizedException {
        Order order = new Order();
        Restaurant restaurant = restaurantService.findById(1);
        order.setRestaurant(restaurant);
        RestaurantTable restaurantTable = restaurantTableService.findById(tableId);
        order.setRestaurantTable(restaurantTable);
        order.setForTakeAway(isForTakeAway);
        List<OrderedItem> orderedItems = Arrays.stream(chosenItems).toList();
        order.setOrderedItems(orderedItems);
        order.setTotalAmount(getTotalAmount(order));
        return order;
    }

    private BigDecimal getTotalAmount(Order order) {
        BigDecimal totalAmount = Money.of(0.00);
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            totalAmount = totalAmount.add(orderedItem.getPrice());
        }
        return Money.of(totalAmount);
    }
}