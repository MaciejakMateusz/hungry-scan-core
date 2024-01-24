package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrderedItem;
import pl.rarytas.rarytas_restaurantside.service.archive.HistoryOrderServiceImpl;
import pl.rarytas.rarytas_restaurantside.service.interfaces.DataTransferService;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataTransferServiceImpl implements DataTransferService {

    private final HistoryOrderServiceImpl historyOrderServiceImpl;

    public DataTransferServiceImpl(HistoryOrderServiceImpl historyOrderServiceImpl) {
        this.historyOrderServiceImpl = historyOrderServiceImpl;
    }

    public void archiveOrder(Order order) {
        HistoryOrder historyOrder = transferOrderDataToHistory(order);
        historyOrderServiceImpl.save(historyOrder);
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
}
