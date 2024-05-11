package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.OrderRepository;
import com.hackybear.hungry_scan_core.repository.OrderSummaryRepository;
import com.hackybear.hungry_scan_core.repository.OrderedItemRepository;
import com.hackybear.hungry_scan_core.service.interfaces.ArchiveDataService;
import com.hackybear.hungry_scan_core.service.interfaces.OrderService;
import com.hackybear.hungry_scan_core.utility.OrderServiceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderSummaryRepository orderSummaryRepository;
    private final MenuItemRepository menuItemRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ArchiveDataService dataTransferService;
    private final OrderServiceHelper orderHelper;
    private final ExceptionHelper exceptionHelper;
    private final OrderedItemRepository orderedItemRepository;

    public OrderServiceImp(OrderRepository orderRepository,
                           OrderSummaryRepository orderSummaryRepository,
                           MenuItemRepository menuItemRepository,
                           SimpMessagingTemplate messagingTemplate,
                           ArchiveDataService dataTransferService,
                           OrderServiceHelper orderHelper,
                           ExceptionHelper exceptionHelper,
                           OrderedItemRepository orderedItemRepository) {
        this.orderRepository = orderRepository;
        this.orderSummaryRepository = orderSummaryRepository;
        this.menuItemRepository = menuItemRepository;
        this.messagingTemplate = messagingTemplate;
        this.dataTransferService = dataTransferService;
        this.orderHelper = orderHelper;
        this.exceptionHelper = exceptionHelper;
        this.orderedItemRepository = orderedItemRepository;
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
    public OrderSummary findByTable(Integer id) throws LocalizedException {
        return orderSummaryRepository.findFirstByRestaurantTableId(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderSummaryService.summaryNotFoundForTable", id));
    }

    @Override
    @Transactional
    public OrderSummary saveDineIn(Order order) throws LocalizedException {
        validateOrder(order);
        saveRefreshAndNotify(order);
        return getOrderSummary(order.getRestaurantTable().getId());
    }

    private OrderSummary getOrderSummary(Integer tableId) {
        return orderSummaryRepository.findFirstByRestaurantTableId(tableId)
                .orElse(new OrderSummary());
    }

    private void validateOrder(Order order) throws LocalizedException {
        RestaurantTable table = order.getRestaurantTable();
        if (Objects.isNull(table)) {
            exceptionHelper.throwLocalizedMessage("error.orderService.invalidTable");
        } else if (!table.isActive()) {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.tableNotActive", table.getId());
        } else if (order.getOrderedItems().isEmpty()) {
            exceptionHelper.throwLocalizedMessage("error.orderService.noOrderedItems");
        }
    }

    @Override
    @Transactional
    public void saveTakeAway(Order order) throws LocalizedException {
        order.setTotalAmount(orderHelper.getOrderAmount(order));
        orderRepository.save(order);
        orderRepository.refresh(order);
        messagingTemplate.convertAndSend("/topic/take-away-orders", findAllTakeAway());
        updateMenuItemsCounterStatistics(order);
    }

    @Override
    public void finishTakeAway(Long id) throws LocalizedException {
        Order existingOrder = findById(id);
        orderHelper.prepareForFinalizingTakeAway(existingOrder);
        orderRepository.saveAndFlush(existingOrder);
        dataTransferService.archiveOrder(existingOrder);
        messagingTemplate.convertAndSend("/topic/take-away-orders", findAllTakeAway());
    }

    @Override
    public void delete(Order order) {
        orderRepository.delete(order);
    }

    private void saveRefreshAndNotify(Order order) throws LocalizedException {
        order.setTotalAmount(orderHelper.getOrderAmount(order));
        linkMenuItemsToOrderedItems(order);
        orderedItemRepository.saveAll(order.getOrderedItems());
        orderRepository.save(order);
        orderRepository.refresh(order);
        persistOrderSummary(order);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
        updateMenuItemsCounterStatistics(order);
    }

    private void persistOrderSummary(Order order) {
        OrderSummary orderSummary = getOrderSummary(order.getRestaurantTable().getId());
        orderSummary.setRestaurant(order.getRestaurant());
        orderSummary.setRestaurantTable(order.getRestaurantTable());
        orderSummary.addOrder(order);
        orderSummary.setTotalAmount(orderHelper.getSummaryAmount(orderSummary));
        if (orderSummary.getOrders().size() == 1) {
            orderSummary.setInitialOrderDate(LocalDate.now());
            orderSummary.setInitialOrderTime(LocalTime.now());
        }
        orderSummaryRepository.saveAndFlush(orderSummary);
    }

    private void linkMenuItemsToOrderedItems(Order order) throws LocalizedException {
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            Integer variantId = orderedItem.getMenuItemVariant().getId();
            MenuItem menuItem = getMenuItemByVariantId(variantId);
            orderedItem.setMenuItem(menuItem);
        }
    }

    private void updateMenuItemsCounterStatistics(Order order) throws LocalizedException {
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            MenuItem menuItem = getMenuItemById(orderedItem.getMenuItem().getId());
            menuItem.setCounter(menuItem.getCounter() + orderedItem.getQuantity());
            menuItemRepository.saveAndFlush(menuItem);
        }
    }

    private MenuItem getMenuItemByVariantId(Integer variantId) throws LocalizedException {
        return menuItemRepository.findByVariantId(variantId)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFoundByVariant", variantId));
    }

    private MenuItem getMenuItemById(Integer id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", id));
    }
}