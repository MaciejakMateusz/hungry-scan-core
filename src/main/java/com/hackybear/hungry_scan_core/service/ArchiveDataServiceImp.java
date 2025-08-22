package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.entity.history.HistoryOrder;
import com.hackybear.hungry_scan_core.entity.history.HistoryOrderSummary;
import com.hackybear.hungry_scan_core.entity.history.HistoryOrderedItem;
import com.hackybear.hungry_scan_core.entity.history.HistoryWaiterCall;
import com.hackybear.hungry_scan_core.repository.OrderSummaryRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantTableRepository;
import com.hackybear.hungry_scan_core.repository.WaiterCallRepository;
import com.hackybear.hungry_scan_core.repository.history.HistoryWaiterCallRepository;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryOrderSummaryService;
import com.hackybear.hungry_scan_core.service.interfaces.ArchiveDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArchiveDataServiceImp implements ArchiveDataService {

    private final HistoryOrderSummaryService historyOrderSummaryService;
    private final OrderSummaryRepository orderSummaryRepository;
    private final WaiterCallRepository waiterCallRepository;
    private final HistoryWaiterCallRepository historyWaiterCallRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    @Override
    public void archiveSummary(OrderSummary orderSummary) {
        HistoryOrderSummary historyOrderSummary = mapSummaryToHistorySummary(orderSummary);
        historyOrderSummaryService.save(historyOrderSummary);
        orderSummaryRepository.delete(orderSummary);
    }

    @Override
    public void archiveOrder(Order order) {

    }

    @Override
    public void archiveWaiterCall(RestaurantTable restaurantTable) {
        historyWaiterCallRepository.saveAll(mapWaiterCallsToHistoryWaiterCalls(restaurantTable));
        waiterCallRepository.deleteAll(restaurantTable.getWaiterCalls());
        restaurantTable.setWaiterCalls(new ArrayList<>());
        restaurantTableRepository.save(restaurantTable);
    }


    private static HistoryOrderSummary mapSummaryToHistorySummary(OrderSummary orderSummary) {
        HistoryOrderSummary historyOrderSummary = getHistoryOrderSummary(orderSummary);

        List<HistoryOrder> historyOrders = orderSummary.getOrders()
                .stream().map(ArchiveDataServiceImp::mapOrderToHistoryOrder).toList();
        historyOrderSummary.setHistoryOrders(historyOrders);

        return historyOrderSummary;
    }

    private static HistoryOrderSummary getHistoryOrderSummary(OrderSummary orderSummary) {
        return new HistoryOrderSummary(
                orderSummary.getId(),
                orderSummary.getRestaurant(),
                orderSummary.getRestaurantTable(),
                orderSummary.getInitialOrderDate(),
                orderSummary.getInitialOrderTime(),
                orderSummary.getTipAmount(),
                orderSummary.getTotalAmount(),
                orderSummary.isPaid(),
                orderSummary.isBillSplitRequested(),
                orderSummary.getPaymentMethod()
        );
    }

    private static HistoryOrder mapOrderToHistoryOrder(Order order) {
        HistoryOrder historyOrder = new HistoryOrder(
                order.getId(),
                order.getRestaurant(),
                order.getRestaurantTable(),
                order.getOrderTime().toLocalDate(),
                order.getOrderTime().toLocalTime(),
                order.getTotalAmount(),
                order.isResolved());

        List<HistoryOrderedItem> transferredItems = order.getOrderedItems()
                .stream().map(ArchiveDataServiceImp::getHistoryOrderedItem)
                .toList();
        historyOrder.setHistoryOrderedItems(transferredItems);

        return historyOrder;
    }

    private static HistoryOrderedItem getHistoryOrderedItem(OrderedItem orderedItem) {
        return new HistoryOrderedItem(
                orderedItem.getId(),
                orderedItem.getMenuItem(),
                orderedItem.getVariant(),
                orderedItem.getAdditionalIngredients(),
                orderedItem.getAdditionalComment(),
                orderedItem.getQuantity(),
                orderedItem.isPaid()
        );
    }

    private static List<HistoryWaiterCall> mapWaiterCallsToHistoryWaiterCalls(RestaurantTable restaurantTable) {
        return restaurantTable.getWaiterCalls()
                .stream()
                .map(waiterCall -> getHistoryWaiterCall(restaurantTable, waiterCall))
                .toList();
    }

    private static HistoryWaiterCall getHistoryWaiterCall(RestaurantTable restaurantTable, WaiterCall waiterCall) {
        return new HistoryWaiterCall(
                waiterCall.getId(),
                restaurantTable.getId(),
                restaurantTable.getNumber(),
                waiterCall.getCallTime(),
                waiterCall.getResolvedTime(),
                waiterCall.isResolved());
    }

}