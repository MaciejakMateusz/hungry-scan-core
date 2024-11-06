package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;

public interface ArchiveDataService {

    void archiveSummary(OrderSummary orderSummary);

    void archiveOrder(Order order);

    void archiveWaiterCall(RestaurantTable restaurantTable);
}
