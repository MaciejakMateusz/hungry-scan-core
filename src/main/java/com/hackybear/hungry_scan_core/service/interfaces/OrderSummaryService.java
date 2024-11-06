package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.OrderSummary;

public interface OrderSummaryService {

    OrderSummary pay(OrderSummary orderSummary);

    void delete(OrderSummary orderSummary);
}
