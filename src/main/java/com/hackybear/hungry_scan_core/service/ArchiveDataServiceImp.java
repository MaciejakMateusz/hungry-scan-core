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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArchiveDataServiceImp implements ArchiveDataService {

    private final HistoryOrderSummaryService historyOrderSummaryService;
    private final OrderSummaryRepository orderSummaryRepository;
    private final WaiterCallRepository waiterCallRepository;
    private final HistoryWaiterCallRepository historyWaiterCallRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public ArchiveDataServiceImp(HistoryOrderSummaryService historyOrderSummaryService,
                                 OrderSummaryRepository orderSummaryRepository,
                                 WaiterCallRepository waiterCallRepository,
                                 HistoryWaiterCallRepository historyWaiterCallRepository,
                                 RestaurantTableRepository restaurantTableRepository) {
        this.historyOrderSummaryService = historyOrderSummaryService;
        this.orderSummaryRepository = orderSummaryRepository;
        this.waiterCallRepository = waiterCallRepository;
        this.historyWaiterCallRepository = historyWaiterCallRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

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


    private HistoryOrderSummary mapSummaryToHistorySummary(OrderSummary orderSummary) {
        HistoryOrderSummary historyOrderSummary = new HistoryOrderSummary(
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

        List<HistoryOrder> historyOrders = new ArrayList<>();
        for (Order order : orderSummary.getOrders()) {
            HistoryOrder historyOrder = mapOrderToHistoryOrder(order);
            historyOrders.add(historyOrder);
        }
        historyOrderSummary.setHistoryOrders(historyOrders);

        return historyOrderSummary;
    }

    private HistoryOrder mapOrderToHistoryOrder(Order order) {
        HistoryOrder historyOrder = new HistoryOrder(
                order.getId(),
                order.getRestaurant(),
                order.getRestaurantTable(),
                order.getOrderTime().toLocalDate(),
                order.getOrderTime().toLocalTime(),
                order.getTotalAmount(),
                order.isResolved());

        List<HistoryOrderedItem> transferredItems = new ArrayList<>();
        order.getOrderedItems().forEach(orderedItem -> {
            HistoryOrderedItem historyOrderedItem = mapOrderedItemToHistoryOrderedItem(orderedItem);
            transferredItems.add(historyOrderedItem);
        });

        historyOrder.setHistoryOrderedItems(transferredItems);

        return historyOrder;
    }

    private HistoryOrderedItem mapOrderedItemToHistoryOrderedItem(OrderedItem orderedItem) {
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

    private List<HistoryWaiterCall> mapWaiterCallsToHistoryWaiterCalls(RestaurantTable restaurantTable) {
        List<HistoryWaiterCall> historyWaiterCalls = new ArrayList<>();
        for (WaiterCall waiterCall : restaurantTable.getWaiterCalls()) {
            historyWaiterCalls.add(new HistoryWaiterCall(
                    waiterCall.getId(),
                    restaurantTable.getId(),
                    restaurantTable.getNumber(),
                    waiterCall.getCallTime(),
                    waiterCall.getResolvedTime(),
                    waiterCall.isResolved()));
        }
        return historyWaiterCalls;
    }

}