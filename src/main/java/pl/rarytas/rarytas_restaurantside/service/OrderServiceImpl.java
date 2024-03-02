package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.repository.archive.HistoryOrderRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.utility.OrderServiceHelper;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WaiterCallServiceImpl waiterCallServiceImpl;
    private final SimpMessagingTemplate messagingTemplate;
    private final ArchiveDataServiceImpl dataTransferServiceImpl;
    private final HistoryOrderRepository historyOrderRepository;
    private final OrderServiceHelper orderHelper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            WaiterCallServiceImpl waiterCallServiceImpl,
                            SimpMessagingTemplate messagingTemplate,
                            ArchiveDataServiceImpl dataTransferServiceImpl,
                            HistoryOrderRepository historyOrderRepository, OrderServiceHelper orderHelper) {
        this.orderRepository = orderRepository;
        this.waiterCallServiceImpl = waiterCallServiceImpl;
        this.messagingTemplate = messagingTemplate;
        this.dataTransferServiceImpl = dataTransferServiceImpl;
        this.historyOrderRepository = historyOrderRepository;
        this.orderHelper = orderHelper;
    }

    @Override
    public List<Order> findAllNotPaid() {
        return orderRepository.findAllNotPaid();
    }

    @Override
    public List<Order> findAllTakeAway() {
        return orderRepository.findAllTakeAway();
    }

    @Override
    public List<Order> findAllByResolvedIsTrue() {
        return orderRepository.findAllResolved();
    }

    @Override
    public Optional<Order> findFinalizedById(Long id, boolean forTakeAway) {
        return orderRepository.findFinalizedById(id, forTakeAway);
    }

    @Override
    public Optional<Order> findByTableNumber(Integer tableNumber) {
        return orderRepository.findNewestOrderByTableNumber(tableNumber);
    }

    @Override
    public Optional<?> findById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return order;
        } else {
            return historyOrderRepository.findById(id);
        }
    }

    @Override
    public void save(Order order) throws LocalizedException {
        if (orderHelper.orderExistsForGivenTable(order)) {
            return;
        }
        saveRefreshAndNotify(order);
    }

    @Override
    public void orderMoreDishes(Order order) {
        Order existingOrder = (Order) findById(order.getId()).orElseThrow();
        addItemsToOrder(existingOrder, order.getOrderedItems());
        saveRefreshAndNotify(existingOrder);
    }

    @Override
    public void saveTakeAway(Order order) {
        orderRepository.save(order);
        orderRepository.refresh(order);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public void requestBill(Order order) throws LocalizedException {
        orderHelper.assertOrderExistsElseThrow(order.getId());
        Order existingOrder = orderRepository.findById(order.getId()).orElseThrow();
        orderHelper.assertWaiterNotCalledElseThrow(existingOrder);
        orderHelper.assertNoBillRequestedElseThrow(existingOrder);
        existingOrder.setBillRequested(true);
        existingOrder.setPaymentMethod(order.getPaymentMethod());
        orderRepository.saveAndFlush(existingOrder);
        assert !existingOrder.isForTakeAway();
        messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
    }

    @Override
    public void finish(Long id, boolean paid, boolean isResolved) throws LocalizedException {
        orderHelper.assertOrderExistsElseThrow(id);
        Order existingOrder = orderRepository.findById(id).orElseThrow();
        orderHelper.assertWaiterNotCalledElseThrow(existingOrder);
        orderHelper.prepareForFinalizing(existingOrder, paid, isResolved);
        orderRepository.saveAndFlush(existingOrder);
        dataTransferServiceImpl.archiveOrder(existingOrder);
        if (!existingOrder.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
        }
        delete(existingOrder);
    }

    @Override
    public void finishTakeAway(Long id,
                               boolean paid,
                               boolean isResolved) throws LocalizedException {
        finish(id, paid, isResolved);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public void callWaiter(Order order) throws LocalizedException {
        orderHelper.assertOrderExistsElseThrow(order.getId());
        Order existingOrder = orderRepository.findById(order.getId()).orElseThrow();
        orderHelper.assertWaiterNotCalledElseThrow(existingOrder);
        orderHelper.assertNoBillRequestedElseThrow(existingOrder);
        existingOrder.setWaiterCalled(true);
        WaiterCall waiterCall = new WaiterCall();
        waiterCall.setOrder(existingOrder);
        orderRepository.saveAndFlush(existingOrder);
        waiterCallServiceImpl.save(waiterCall);
        messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
    }

    @Override
    public void resolveWaiterCall(Long id) throws LocalizedException {
        orderHelper.assertOrderExistsElseThrow(id);
        Order order = orderRepository.findById(id).orElseThrow();
        order.setWaiterCalled(false);
        WaiterCall waiterCall = waiterCallServiceImpl.findByOrderAndResolved(order, false).orElseThrow();
        waiterCall.setOrder(order);
        waiterCall.setResolved(true);
        orderRepository.saveAndFlush(order);
        waiterCallServiceImpl.save(waiterCall);
    }

    @Override
    public void delete(Order order) {
        orderRepository.delete(order);
    }

    private void addItemsToOrder(Order existingOrder, List<OrderedItem> newItems) {
        existingOrder.getOrderedItems().addAll(newItems);
    }

    private void saveRefreshAndNotify(Order order) {
        orderRepository.saveAndFlush(order);
        orderRepository.refresh(order);
        if (!order.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
        }
    }
}
