package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryWaiterCall;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryWaiterCallService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.ArchiveDataService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArchiveDataServiceImpl implements ArchiveDataService {

    private final HistoryOrderService historyOrderService;
    private final WaiterCallService waiterCallService;
    private final HistoryWaiterCallService historyWaiterCallService;
    private final ExceptionHelper exceptionHelper;

    public ArchiveDataServiceImpl(HistoryOrderService historyOrderService,
                                  WaiterCallService waiterCallService,
                                  HistoryWaiterCallService historyWaiterCallService, ExceptionHelper exceptionHelper) {
        this.historyOrderService = historyOrderService;
        this.waiterCallService = waiterCallService;
        this.historyWaiterCallService = historyWaiterCallService;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void archiveOrder(Order order) throws LocalizedException {
        if(!order.isPaid()) {
            exceptionHelper.throwLocalizedMessage("error.archiveDataService.orderNotPaid", order.getId());
        }
        HistoryOrder historyOrder = mapOrderToHistoryOrder(order);
        historyOrderService.save(historyOrder);
        transferWaiterCallDataToHistory(order, historyOrder);
    }

    private HistoryOrder mapOrderToHistoryOrder(Order order) {
        HistoryOrder historyOrder = new HistoryOrder(
                order.getId(),
                order.getRestaurantTable(),
                order.getRestaurant(),
                order.getOrderTime().toLocalDate(),
                order.getOrderTime().toLocalTime(),
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
            historyOrderedItem.setId(orderedItem.getId());
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
