package pl.rarytas.hungry_scan_core.service;

import org.springframework.stereotype.Component;
import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.entity.OrderSummary;
import pl.rarytas.hungry_scan_core.entity.WaiterCall;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrder;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrderSummary;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrderedItem;
import pl.rarytas.hungry_scan_core.entity.history.HistoryWaiterCall;
import pl.rarytas.hungry_scan_core.repository.OrderSummaryRepository;
import pl.rarytas.hungry_scan_core.service.history.interfaces.HistoryOrderService;
import pl.rarytas.hungry_scan_core.service.history.interfaces.HistoryOrderSummaryService;
import pl.rarytas.hungry_scan_core.service.history.interfaces.HistoryWaiterCallService;
import pl.rarytas.hungry_scan_core.service.interfaces.ArchiveDataService;
import pl.rarytas.hungry_scan_core.service.interfaces.WaiterCallService;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArchiveDataServiceImp implements ArchiveDataService {

    private final HistoryOrderService historyOrderService;
    private final HistoryOrderSummaryService historyOrderSummaryService;
    private final OrderSummaryRepository orderSummaryRepository;
    private final WaiterCallService waiterCallService;
    private final HistoryWaiterCallService historyWaiterCallService;

    public ArchiveDataServiceImp(HistoryOrderService historyOrderService,
                                 HistoryOrderSummaryService historyOrderSummaryService,
                                 OrderSummaryRepository orderSummaryRepository,
                                 WaiterCallService waiterCallService,
                                 HistoryWaiterCallService historyWaiterCallService) {
        this.historyOrderService = historyOrderService;
        this.historyOrderSummaryService = historyOrderSummaryService;
        this.orderSummaryRepository = orderSummaryRepository;
        this.waiterCallService = waiterCallService;
        this.historyWaiterCallService = historyWaiterCallService;
    }

    @Override
    public void archiveSummary(OrderSummary orderSummary) {
        HistoryOrderSummary historyOrderSummary = mapSummaryToHistorySummary(orderSummary);
        historyOrderSummaryService.save(historyOrderSummary);
        orderSummaryRepository.delete(orderSummary);
    }

    @Override
    public void archiveOrder(Order order) {
        HistoryOrder historyOrder = mapOrderToHistoryOrder(order);
        historyOrderService.save(historyOrder);
        transferWaiterCallDataToHistory(order, historyOrder);
    }

    private HistoryOrderSummary mapSummaryToHistorySummary(OrderSummary orderSummary) {
        HistoryOrderSummary historyOrderSummary = new HistoryOrderSummary(
                orderSummary.getId(),
                orderSummary.getRestaurantTable(),
                orderSummary.getRestaurant(),
                orderSummary.getInitialOrderDate(),
                orderSummary.getInitialOrderTime(),
                orderSummary.getTipAmount(),
                orderSummary.getTotalAmount(),
                orderSummary.isPaid(),
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
            HistoryOrderedItem historyOrderedItem = new HistoryOrderedItem();
            historyOrderedItem.setId(orderedItem.getId());
            historyOrderedItem.setMenuItemVariant(orderedItem.getMenuItemVariant());
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
