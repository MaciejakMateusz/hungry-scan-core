package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.utility.OrderServiceHelper;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImp implements OrderService {
    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ArchiveDataServiceImp dataTransferServiceImpl;
    private final OrderServiceHelper orderHelper;
    private final ExceptionHelper exceptionHelper;

    public OrderServiceImp(OrderRepository orderRepository,
                           SimpMessagingTemplate messagingTemplate,
                           ArchiveDataServiceImp dataTransferServiceImpl,
                           OrderServiceHelper orderHelper, ExceptionHelper exceptionHelper) {
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
        this.dataTransferServiceImpl = dataTransferServiceImpl;
        this.orderHelper = orderHelper;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
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
                        "error.orderService.orderNotFound", id));
    }

    @Override
    public Order findByTableNumber(Integer tableNumber) throws LocalizedException {
        return orderRepository.findNewestOrderByTableNumber(tableNumber)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.orderNotFoundByTable", tableNumber));
    }

    @Override
    @Transactional
    public void save(Order order) throws LocalizedException {
        if (orderHelper.orderExistsForGivenTable(order)) {
            exceptionHelper.throwLocalizedMessage("error.orderService.orderExistsForTable");
            return;
        }
        saveRefreshAndNotify(order);
    }

    @Override
    @Transactional
    public void saveTakeAway(Order order) {
        order.setTotalAmount(orderHelper.calculateTotalAmount(order));
        orderRepository.save(order);
        orderRepository.refresh(order);
        messagingTemplate.convertAndSend("/topic/take-away-orders", findAllTakeAway());
    }

    @Override
    @Transactional
    public void orderMoreDishes(Order order) throws LocalizedException {
        Order existingOrder = findById(order.getId());
        existingOrder.addToOrderedItems(order.getOrderedItems());
        saveRefreshAndNotify(existingOrder);
    }

    @Override
    @Transactional
    public void tip(Long id, BigDecimal tipAmount) throws LocalizedException {
        if (tipAmount.compareTo(BigDecimal.ZERO) <= 0) {
            exceptionHelper.throwLocalizedMessage("error.orderService.invalidTipAmount");
        }
        Order existingOrder = findById(id);
        existingOrder.setTipAmount(tipAmount);
        saveRefreshAndNotify(existingOrder);
    }

    @Override
    public void finish(Long id) throws LocalizedException {
        Order existingOrder = findById(id);
        orderHelper.prepareForFinalizingDineIn(existingOrder);
        orderRepository.saveAndFlush(existingOrder);
        dataTransferServiceImpl.archiveOrder(existingOrder);
        delete(existingOrder);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
    }

    @Override
    public void finishTakeAway(Long id) throws LocalizedException {
        Order existingOrder = findById(id);
        orderHelper.prepareForFinalizingTakeAway(existingOrder);
        orderRepository.saveAndFlush(existingOrder);
        dataTransferServiceImpl.archiveOrder(existingOrder);
        delete(existingOrder);
        messagingTemplate.convertAndSend("/topic/take-away-orders", findAllTakeAway());
    }

    @Override
    public void delete(Order order) {
        orderRepository.delete(order);
    }

    private void saveRefreshAndNotify(Order order) {
        order.setTotalAmount(orderHelper.calculateTotalAmount(order));
        orderRepository.saveAndFlush(order);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
    }
}
