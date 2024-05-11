package pl.rarytas.hungry_scan_core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.entity.OrderedItem;
import pl.rarytas.hungry_scan_core.exception.ExceptionHelper;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.repository.OrderRepository;
import pl.rarytas.hungry_scan_core.repository.OrderedItemRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.OrderedItemService;

import java.util.List;

@Slf4j
@Service
public class OrderedItemServiceImp implements OrderedItemService {

    private final OrderedItemRepository orderedItemRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderRepository orderRepository;
    private final ExceptionHelper exceptionHelper;

    public OrderedItemServiceImp(OrderedItemRepository orderedItemRepository, SimpMessagingTemplate messagingTemplate, OrderRepository orderRepository, ExceptionHelper exceptionHelper) {
        this.orderedItemRepository = orderedItemRepository;
        this.messagingTemplate = messagingTemplate;
        this.orderRepository = orderRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<OrderedItem> findAll() {
        return orderedItemRepository.findAll();
    }

    @Override
    public OrderedItem findById(Long id) throws LocalizedException {
        return orderedItemRepository.findById(id).orElseThrow(exceptionHelper.supplyLocalizedMessage(
                "error.orderedItemService.orderedItemNotFound", id));
    }

    @Override
    @Transactional
    public void saveAll(List<OrderedItem> orderedItems) {
        orderedItemRepository.saveAllAndFlush(orderedItems);
    }

    @Override
    @Transactional
    @Deprecated
    //TODO metoda do wywalenia
    public void toggleIsReadyToServe(Long id) {
        OrderedItem orderedItem;
        try {
            orderedItem = findById(id);
        } catch (LocalizedException e) {
            log.error(e.getLocalizedMessage());
            return;
        }
//        orderedItem.setReadyToServe(!orderedItem.isReadyToServe());
        orderedItemRepository.saveAndFlush(orderedItem);
        orderedItemRepository.refresh(orderedItem);
        List<Order> orders = orderRepository.findAllDineIn();
        messagingTemplate.convertAndSend("/topic/restaurant-orders", orders);
    }

}