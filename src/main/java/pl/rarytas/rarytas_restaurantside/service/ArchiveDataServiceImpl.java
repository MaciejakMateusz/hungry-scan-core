package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryWaiterCall;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryWaiterCallService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.ArchiveDataService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArchiveDataServiceImpl implements ArchiveDataService {

    private final HistoryOrderService historyOrderService;
    private final WaiterCallService waiterCallService;
    private final HistoryWaiterCallService historyWaiterCallService;

    public ArchiveDataServiceImpl(HistoryOrderService historyOrderService,
                                  WaiterCallService waiterCallService,
                                  HistoryWaiterCallService historyWaiterCallService) {
        this.historyOrderService = historyOrderService;
        this.waiterCallService = waiterCallService;
        this.historyWaiterCallService = historyWaiterCallService;
    }

    @Override
    public void archiveOrder(Order order) {
        HistoryOrder historyOrder = mapOrderToHistoryOrder(order);
        historyOrderService.save(historyOrder);
        transferWaiterCallDataToHistory(order, historyOrder);
    }

    private HistoryOrder mapOrderToHistoryOrder(Order order) {
        HistoryOrder historyOrder = new HistoryOrder(
                order.getId(),
                order.getRestaurantTable(),
                order.getRestaurant(),
                order.getOrderTime(),
                order.getPaymentMethod(),
                order.getTotalAmount(),
                order.isPaid(),
                order.isForTakeAway(),
                order.isBillRequested(),
                order.isResolved(),
                order.isWaiterCalled(),
                order.getOrderNumber());

        List<HistoryOrderedItem> transferredItems = new ArrayList<>();
        order.getOrderedItems().forEach(orderedItem -> {
            HistoryOrderedItem historyOrderedItem = new HistoryOrderedItem();
            historyOrderedItem.setId(Long.valueOf(orderedItem.getId()));
            historyOrderedItem.setMenuItem(orderedItem.getMenuItem());
            historyOrderedItem.setQuantity(orderedItem.getQuantity());
            transferredItems.add(historyOrderedItem);
        });

        historyOrder.setHistoryOrderedItems(transferredItems);

        return historyOrder;
    }

    private void transferWaiterCallDataToHistory(Order order, HistoryOrder historyOrder) {
        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(order);
        HistoryWaiterCall historyWaiterCall = new HistoryWaiterCall();

        if (!waiterCalls.isEmpty()) {
            waiterCalls.forEach(waiterCall -> {
                historyWaiterCall.setId(Long.valueOf(waiterCall.getId()));
                historyWaiterCall.setCallTime(waiterCall.getCallTime());
                historyWaiterCall.setResolvedTime(waiterCall.getResolvedTime());
                historyWaiterCall.setResolved(waiterCall.isResolved());
                historyWaiterCall.setHistoryOrder(historyOrder);

                historyWaiterCallService.save(historyWaiterCall);
                waiterCallService.delete(waiterCall);
            });
        }
    }
}
