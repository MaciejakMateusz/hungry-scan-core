package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryWaiterCall;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryWaiterCallService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.DataTransferService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataTransferServiceImpl implements DataTransferService {

    private final HistoryOrderService historyOrderService;
    private final WaiterCallService waiterCallService;
    private final HistoryWaiterCallService historyWaiterCallService;

    public DataTransferServiceImpl(HistoryOrderService historyOrderService, WaiterCallService waiterCallService, HistoryWaiterCallService historyWaiterCallService) {
        this.historyOrderService = historyOrderService;
        this.waiterCallService = waiterCallService;
        this.historyWaiterCallService = historyWaiterCallService;
    }

    public void archiveOrder(Order order) {
        HistoryOrder historyOrder = transferOrderDataToHistory(order);
        historyOrderService.save(historyOrder);

        transferWaiterCallDataToHistory(order);
    }

    private HistoryOrder transferOrderDataToHistory(Order order) {
        HistoryOrder historyOrder = new HistoryOrder();

        List<HistoryOrderedItem> transferredItems = new ArrayList<>();
        order.getOrderedItems().forEach(orderedItem -> {
            HistoryOrderedItem historyOrderedItem = new HistoryOrderedItem();
            historyOrderedItem.setId(Long.valueOf(orderedItem.getId()));
            historyOrderedItem.setMenuItem(orderedItem.getMenuItem());
            historyOrderedItem.setQuantity(orderedItem.getQuantity());
            transferredItems.add(historyOrderedItem);
        });

        historyOrder.setHistoryOrderedItems(transferredItems);
        historyOrder.setId(Long.valueOf(order.getId()));
        historyOrder.setOrderNumber(order.getOrderNumber());
        historyOrder.setOrderTime(order.getOrderTime());
        historyOrder.setPaid(order.isPaid());
        historyOrder.setResolved(order.isResolved());
        historyOrder.setRestaurant(order.getRestaurant());
        historyOrder.setBillRequested(order.isBillRequested());
        historyOrder.setForTakeAway(order.isForTakeAway());
        historyOrder.setPaymentMethod(order.getPaymentMethod());
        historyOrder.setRestaurantTable(order.getRestaurantTable());
        historyOrder.setTotalAmount(order.getTotalAmount());
        historyOrder.setWaiterCalled(order.isWaiterCalled());

        return historyOrder;
    }

    private void transferWaiterCallDataToHistory(Order order) {
        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(order);
        HistoryWaiterCall historyWaiterCall = new HistoryWaiterCall();

        waiterCalls.forEach(waiterCall -> {
            historyWaiterCall.setId(Long.valueOf(waiterCall.getId()));
            historyWaiterCall.setCallTime(waiterCall.getCallTime());
            historyWaiterCall.setResolvedTime(waiterCall.getResolvedTime());
            historyWaiterCall.setResolved(waiterCall.isResolved());
            historyWaiterCall.setOrder(waiterCall.getOrder());

            historyWaiterCallService.save(historyWaiterCall);
            waiterCallService.delete(waiterCall);
        });
    }
}
