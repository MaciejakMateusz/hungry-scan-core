package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderServiceInterface;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class OrderService implements OrderServiceInterface {
    private final OrderRepository orderRepository;
    private final WaiterCallService waiterCallService;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderService(OrderRepository orderRepository, WaiterCallService waiterCallService, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
        this.waiterCallService = waiterCallService;
        this.messagingTemplate = messagingTemplate;
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
    public List<Order> findAllFinalized(boolean forTakeAway,
                                        Integer limit,
                                        Integer offset) {
        return orderRepository.findFinalizedOrders(forTakeAway, limit, offset);
    }

    @Override
    public Optional<Order> findFinalizedById(Integer id, boolean forTakeAway) {
        return orderRepository.findFinalizedById(id, forTakeAway);
    }

    @Override
    public List<Order> findFinalizedByDate(String date, boolean forTakeAway) {
        return orderRepository.findFinalizedByDate(date, forTakeAway);
    }

    @Override
    public List<Order> findAllResolvedTakeAwayLimit50() {
        return orderRepository.findAllResolvedTakeAwayLimit50();
    }

    @Override
    public Optional<Order> findByTableNumber(Integer number) {
        return orderRepository.findNewestOrderByTableNumber(number);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public void save(Order order) {

        if (orderRepository.existsByRestaurantTable(order.getRestaurantTable())) {
            Order existingOrder = orderRepository
                    .findNewestOrderByTableNumber(order.getRestaurantTable().getId())
                    .orElseThrow();
            if (!existingOrder.isResolved()) {
                log.warn("Order with given table number already exists");
                return;
            }
        }
        orderRepository.save(order);
        orderRepository.refresh(order);
        if (!order.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-order", findAllNotPaid());
        }
    }

    @Override
    public void saveTakeAway(Order order) {
        orderRepository.save(order);
        orderRepository.refresh(order);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public void patch(Order order) {
        Order existingOrder = orderRepository
                .findById(order.getId())
                .orElseThrow();
        if (existingOrder.isResolved()) {
            return;
        }
        order.setOrderTime(existingOrder.getOrderTime());
        order.setOrderNumber(existingOrder.getOrderNumber());
        order.setRestaurant(existingOrder.getRestaurant());
        order.setRestaurantTable(existingOrder.getRestaurantTable());
        if (!"Brak".equals(existingOrder.getPaymentMethod())) {
            order.setPaymentMethod(existingOrder.getPaymentMethod());
        }
        if (Objects.isNull(order.getOrderedItems())) {
            order.setOrderedItems(existingOrder.getOrderedItems());
        } else {
            existingOrder.getOrderedItems().addAll(order.getOrderedItems());
        }
        if (!order.isBillRequested()) {
            order.setBillRequested(existingOrder.isBillRequested());
        }

        orderRepository.saveAndFlush(order);

        if (!order.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-order", findAllNotPaid());
            messagingTemplate.convertAndSend("/topic/dineIn-orders", findAllNotPaid());
        }
    }

    @Override
    public void patchTakeAway(Order order) {
        patch(order);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public void finish(Integer id, boolean paid, boolean isResolved) {
        Order existingOrder = orderRepository
                .findById(id)
                .orElseThrow();
        existingOrder.setPaid(paid);
        existingOrder.setResolved(isResolved);
        existingOrder.setTotalAmount(calculateTotalAmount(existingOrder));
        if (!existingOrder.isForTakeAway()) {
            existingOrder.setBillRequested(true);
        }
        orderRepository.saveAndFlush(existingOrder);
        if (!existingOrder.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-order", findAllNotPaid());
            messagingTemplate.convertAndSend("/topic/dineIn-orders", findAllNotPaid());
        }
    }

    @Override
    public void finishTakeAway(Integer id,
                               boolean paid,
                               boolean isResolved) {
        finish(id, paid, isResolved);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public void callWaiter(Order order) {
        WaiterCall waiterCall = new WaiterCall();
        waiterCall.setOrder(order);
        if (!order.isWaiterCalled()) {
            waiterCall.setResolved(true);
        }
        patch(order);
        waiterCallService.callWaiter(waiterCall);
    }

    @Override
    public void resolveWaiterCall(Integer id, boolean waiterCalled) {
        Order order = new Order();
        if (orderRepository.existsById(id)) {
            order = orderRepository.findById(id).orElseThrow();
            order.setWaiterCalled(waiterCalled);
        }
        WaiterCall waiterCall = new WaiterCall();
        waiterCall.setOrder(order);
        waiterCall.setResolved(true);
        orderRepository.saveAndFlush(order);
        waiterCallService.callWaiter(waiterCall);
    }

    private BigDecimal calculateTotalAmount(Order order) {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            BigDecimal itemPrice = orderedItem.getMenuItem().getPrice();
            int quantity = orderedItem.getQuantity();
            sum = sum.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }
        return sum;
    }
}
