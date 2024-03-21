package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.repository.OrderedItemRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;

import java.util.List;

@Slf4j
@Service
public class OrderedItemServiceImpl implements OrderedItemService {

    private final OrderedItemRepository orderedItemRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderRepository orderRepository;
    private final ExceptionHelper exceptionHelper;

    public OrderedItemServiceImpl(OrderedItemRepository orderedItemRepository, SimpMessagingTemplate messagingTemplate, OrderRepository orderRepository, ExceptionHelper exceptionHelper) {
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
    public void toggleIsReadyToServe(Long id) {
        OrderedItem orderedItem;
        try {
            orderedItem = findById(id);
        } catch (LocalizedException e) {
            log.error(e.getLocalizedMessage());
            return;
        }
        orderedItem.setReadyToServe(!orderedItem.isReadyToServe());
        orderedItemRepository.saveAndFlush(orderedItem);
        orderedItemRepository.refresh(orderedItem);
        List<Order> orders = orderRepository.findAllDineIn();
        messagingTemplate.convertAndSend("/topic/restaurant-orders", orders);
    }

}