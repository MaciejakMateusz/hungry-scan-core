package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.utility.OrderServiceHelper;

import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WaiterCallServiceImpl waiterCallServiceImpl;
    private final SimpMessagingTemplate messagingTemplate;
    private final ArchiveDataServiceImpl dataTransferServiceImpl;
    private final OrderServiceHelper orderHelper;
    private final ExceptionHelper exceptionHelper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            WaiterCallServiceImpl waiterCallServiceImpl,
                            SimpMessagingTemplate messagingTemplate,
                            ArchiveDataServiceImpl dataTransferServiceImpl,
                            OrderServiceHelper orderHelper, ExceptionHelper exceptionHelper) {
        this.orderRepository = orderRepository;
        this.waiterCallServiceImpl = waiterCallServiceImpl;
        this.messagingTemplate = messagingTemplate;
        this.dataTransferServiceImpl = dataTransferServiceImpl;
        this.orderHelper = orderHelper;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAllDineIn();
    }

    @Override
    public List<Order> findAllTakeAway() {
        return orderRepository.findAllTakeAway();
    }

    @Override
    public List<Order> findAllDineIn() {
        return orderRepository.findAllDineIn();
    }

    @Override
    public Order findById(Long id) throws LocalizedException {
        return orderRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.general.orderNotFound", id));
    }

    @Override
    public Order findByTableNumber(Integer tableNumber) throws LocalizedException {
        return orderRepository.findNewestOrderByTableNumber(tableNumber)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.general.orderNotFoundByTable", tableNumber));
    }

    @Override
    public void save(Order order) throws LocalizedException {
        if (orderHelper.orderExistsForGivenTable(order)) {
            return;
        }
        saveRefreshAndNotify(order);
    }

    @Override
    public void orderMoreDishes(Order order) throws LocalizedException {
        Order existingOrder = findById(order.getId());
        addItemsToOrder(existingOrder, order.getOrderedItems());
        saveRefreshAndNotify(existingOrder);
    }

    @Override
    public void saveTakeAway(Order order) {
        orderRepository.save(order);
        orderRepository.refresh(order);
        messagingTemplate.convertAndSend("/topic/take-away-orders", findAllTakeAway());
    }

    @Override
    public void requestBill(Order order) throws LocalizedException {
        orderHelper.assertOrderExistsElseThrow(order.getId());
        Order existingOrder = orderRepository.findById(order.getId()).orElseThrow();
        orderHelper.assertWaiterNotCalledElseThrow(existingOrder);
        orderHelper.assertBillNotRequestedElseThrow(existingOrder);
        existingOrder.setBillRequested(true);
        existingOrder.setPaymentMethod(order.getPaymentMethod());
        orderRepository.saveAndFlush(existingOrder);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
    }

    @Override
    public void finish(Long id) throws LocalizedException {
        orderHelper.assertOrderExistsElseThrow(id);
        Order existingOrder = findById(id);
        orderHelper.assertWaiterNotCalledElseThrow(existingOrder);
        orderHelper.prepareForFinalizingDineIn(existingOrder);
        orderRepository.saveAndFlush(existingOrder);
        dataTransferServiceImpl.archiveOrder(existingOrder);
        delete(existingOrder);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
    }

    @Override
    public void finishTakeAway(Long id) throws LocalizedException {
        orderHelper.assertOrderExistsElseThrow(id);
        Order existingOrder = findById(id);
        orderHelper.prepareForFinalizingTakeAway(existingOrder);
        orderRepository.saveAndFlush(existingOrder);
        dataTransferServiceImpl.archiveOrder(existingOrder);
        delete(existingOrder);
        messagingTemplate.convertAndSend("/topic/take-away-orders", findAllTakeAway());
    }

    @Override
    public void callWaiter(Order order) throws LocalizedException {
        orderHelper.assertOrderExistsElseThrow(order.getId());
        Order existingOrder = orderRepository.findById(order.getId()).orElseThrow();
        orderHelper.assertWaiterNotCalledElseThrow(existingOrder);
        orderHelper.assertBillNotRequestedElseThrow(existingOrder);
        existingOrder.setWaiterCalled(true);
        WaiterCall waiterCall = new WaiterCall();
        waiterCall.setOrder(existingOrder);
        orderRepository.saveAndFlush(existingOrder);
        waiterCallServiceImpl.save(waiterCall);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
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
        orderRepository.refresh(order);
    }

    private void addItemsToOrder(Order existingOrder, List<OrderedItem> newItems) {
        existingOrder.getOrderedItems().addAll(newItems);
    }

    private void saveRefreshAndNotify(Order order) {
        orderRepository.saveAndFlush(order);
        orderRepository.refresh(order);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
    }
}
