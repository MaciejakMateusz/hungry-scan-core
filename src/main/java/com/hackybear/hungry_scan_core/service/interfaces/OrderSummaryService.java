package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

public interface OrderSummaryService {

    OrderSummary findByTableNumber(Integer tableNumber) throws LocalizedException;

    OrderSummary pay(OrderSummary orderSummary);

    void delete(OrderSummary orderSummary);
}
