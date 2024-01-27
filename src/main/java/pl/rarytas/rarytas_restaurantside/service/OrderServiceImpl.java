package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WaiterCallServiceImpl waiterCallServiceImpl;
    private final SimpMessagingTemplate messagingTemplate;
    private final DataTransferServiceImpl dataTransferServiceImpl;

    public OrderServiceImpl(OrderRepository orderRepository,
                            WaiterCallServiceImpl waiterCallServiceImpl,
                            SimpMessagingTemplate messagingTemplate,
                            DataTransferServiceImpl dataTransferServiceImpl) {
        this.orderRepository = orderRepository;
        this.waiterCallServiceImpl = waiterCallServiceImpl;
        this.messagingTemplate = messagingTemplate;
        this.dataTransferServiceImpl = dataTransferServiceImpl;
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
    public Optional<Order> findFinalizedById(Integer id, boolean forTakeAway) {
        return orderRepository.findFinalizedById(id, forTakeAway);
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
        if (orderExistsForGivenTable(order)) {
            return;
        }
        orderRepository.save(order);
        orderRepository.refresh(order);
        if (!order.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
        }
    }

    private boolean orderExistsForGivenTable(Order order) {
        if (orderRepository.existsByRestaurantTable(order.getRestaurantTable())) {
            Order existingOrder = orderRepository
                    .findNewestOrderByTableNumber(order.getRestaurantTable().getId())
                    .orElseThrow();
            if (!existingOrder.isResolved()) {
                log.warn("Order with given table number already exists");
                return true;
            }
        }
        return false;
    }

    @Override
    public void saveTakeAway(Order order) {
        orderRepository.save(order);
        orderRepository.refresh(order);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public void patch(Order order) {

        if(!orderRepository.existsById(order.getId())){
            log.warn("Order with ID = " + order.getId() + " doesn't exist.");
            return;
        }

        Order existingOrder = orderRepository
                .findById(order.getId())
                .orElseThrow();

        if (existingOrder.isResolved()) {
            return;
        }

        orderRepository.saveAndFlush(order);

        if (!order.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
        }
    }


    @Override
    public void patchTakeAway(Order order) {
        if(!orderRepository.existsById(order.getId())) {
            log.warn("Order with ID = " + order.getId() + " doesn't exist.");
            return;
        }
        Order existingOrder = orderRepository.findById(order.getId()).orElseThrow();
        orderRepository.saveAndFlush(existingOrder);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public void requestBill(Order order) {
        if(!orderRepository.existsById(order.getId())) {
            log.warn("Order with ID = " + order.getId() + " doesn't exist.");
            return;
        }
        Order existingOrder = orderRepository.findById(order.getId()).orElseThrow();
        if(existingOrder.isWaiterCalled()) {
            log.warn("Order with ID = " + existingOrder.getId() + " has active waiter call - bill can't be requested");
            return;
        }
        existingOrder.setBillRequested(true);
        existingOrder.setPaymentMethod(order.getPaymentMethod());
        orderRepository.saveAndFlush(existingOrder);
        if (!order.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
        }
    }

    @Override
    public void finish(Integer id, boolean paid, boolean isResolved) {
        if(!orderRepository.existsById(id)) {
            log.warn("Order with ID = " + id + " doesn't exist.");
            return;
        }

        Order existingOrder = orderRepository
                .findById(id)
                .orElseThrow();

        if(existingOrder.isWaiterCalled()) {
            log.warn("Order with ID = " + id + " has active waiter call - it can't be finalized");
            return;
        }

        existingOrder.setPaid(paid);
        existingOrder.setResolved(isResolved);
        existingOrder.setTotalAmount(calculateTotalAmount(existingOrder));
        if (!existingOrder.isForTakeAway()) {
            existingOrder.setBillRequested(true);
        }
        orderRepository.saveAndFlush(existingOrder);
        dataTransferServiceImpl.archiveOrder(existingOrder);
        if (!existingOrder.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
        }
        delete(existingOrder);
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
        if(!orderRepository.existsById(order.getId())) {
            log.warn("Order with ID = " + order.getId() + " doesn't exist.");
            return;
        }

        Order existingOrder = orderRepository.findById(order.getId()).orElseThrow();
        if(existingOrder.isBillRequested()) {
            log.warn("Order with ID = " + existingOrder.getId() + " has requested a bill and waiter can't be called.");
            return;
        } else if (existingOrder.isWaiterCalled()) {
            log.warn("Order with ID = " + existingOrder.getId() + " has already called a waiter.");
            return;
        }
        existingOrder.setWaiterCalled(true);
        WaiterCall waiterCall = new WaiterCall();
        waiterCall.setOrder(existingOrder);
        orderRepository.saveAndFlush(existingOrder);
        waiterCallServiceImpl.save(waiterCall);
        messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
    }

    @Override
    public void resolveWaiterCall(Integer id) {
        Order order = new Order();
        if (orderRepository.existsById(id)) {
            order = orderRepository.findById(id).orElseThrow();
            order.setWaiterCalled(false);
        }
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
