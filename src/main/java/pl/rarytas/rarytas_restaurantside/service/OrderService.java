package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderServiceInterface;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class OrderService implements OrderServiceInterface {
    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderService(OrderRepository orderRepository, SimpMessagingTemplate messagingTemplate) {
        this.orderRepository = orderRepository;
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
    public List<Order> findAllPaidLimit50() {
        return orderRepository.findAllPaidLimit50();
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
        orderRepository.refresh(order);
        if (!order.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-order", findAllNotPaid());
        }
    }

    @Override
    public void saveTakeAway(Order order) {
        save(order);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public void patch(Order order) {
        Order existingOrder = orderRepository
                .findById(order.getId())
                .orElseThrow();
        order.setOrderTime(existingOrder.getOrderTime());
        order.setOrderNumber(existingOrder.getOrderNumber());
        order.setRestaurant(existingOrder.getRestaurant());
        order.setRestaurantTable(existingOrder.getRestaurantTable());
        if (Objects.nonNull(existingOrder.getPaymentMethod())) {
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

    public void finish(Integer id, boolean paid) {
        Order existingOrder = orderRepository
                .findById(id)
                .orElseThrow();
        existingOrder.setPaid(paid);
        existingOrder.setBillRequested(true);
        orderRepository.saveAndFlush(existingOrder);
        if (!existingOrder.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-order", findAllNotPaid());
            messagingTemplate.convertAndSend("/topic/dineIn-orders", findAllNotPaid());
        }
    }

    @Override
    public void finishTakeAway(Integer id, boolean paid) {
        finish(id, paid);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }
}
