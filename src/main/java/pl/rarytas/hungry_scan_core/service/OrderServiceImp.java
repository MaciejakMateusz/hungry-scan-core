package pl.rarytas.hungry_scan_core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.hungry_scan_core.entity.MenuItem;
import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.entity.OrderedItem;
import pl.rarytas.hungry_scan_core.exception.ExceptionHelper;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.repository.MenuItemRepository;
import pl.rarytas.hungry_scan_core.repository.OrderRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.ArchiveDataService;
import pl.rarytas.hungry_scan_core.service.interfaces.OrderService;
import pl.rarytas.hungry_scan_core.utility.OrderServiceHelper;

import java.util.List;

@Service
@Slf4j
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ArchiveDataService dataTransferService;
    private final OrderServiceHelper orderHelper;
    private final ExceptionHelper exceptionHelper;

    public OrderServiceImp(OrderRepository orderRepository, MenuItemRepository menuItemRepository,
                           SimpMessagingTemplate messagingTemplate,
                           ArchiveDataService dataTransferService,
                           OrderServiceHelper orderHelper, ExceptionHelper exceptionHelper) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.messagingTemplate = messagingTemplate;
        this.dataTransferService = dataTransferService;
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
    @Transactional
    public void saveDineIn(Order order) throws LocalizedException {
        saveRefreshAndNotify(order);
    }

    @Override
    @Transactional
    public void saveTakeAway(Order order) throws LocalizedException {
        order.setTotalAmount(orderHelper.calculateTotalAmount(order));
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
        order.setTotalAmount(orderHelper.calculateTotalAmount(order));
        linkMenuItemsToOrderedItems(order);
        orderRepository.saveAndFlush(order);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
        updateMenuItemsCounterStatistics(order);
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