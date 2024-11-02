package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface OrderService {
    List<Order> findAll();

    List<Order> findAllTakeAway();

    List<Order> findAllDineIn();

    Order findById(Long id) throws LocalizedException;

    OrderSummary findByTable(Long id) throws LocalizedException;

    OrderSummary saveDineIn(Order order) throws LocalizedException;

    void saveTakeAway(Order order) throws LocalizedException;

    void finishTakeAway(Long id) throws LocalizedException;

    void delete(Order order);
}
