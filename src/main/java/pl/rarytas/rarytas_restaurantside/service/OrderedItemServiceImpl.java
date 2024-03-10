package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.repository.OrderedItemRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderedItemServiceImpl implements OrderedItemService {

    private final OrderedItemRepository orderedItemRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderRepository orderRepository;

    public OrderedItemServiceImpl(OrderedItemRepository orderedItemRepository, SimpMessagingTemplate messagingTemplate, OrderRepository orderRepository) {
        this.orderedItemRepository = orderedItemRepository;
        this.messagingTemplate = messagingTemplate;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderedItem> findAll() {
        return orderedItemRepository.findAll();
    }

    @Override
    public Optional<OrderedItem> findById(Long id) {
        return orderedItemRepository.findById(id);
    }

    @Override
    public void delete(OrderedItem orderedItem) {
        orderedItemRepository.delete(orderedItem);
    }

    @Override
    @Transactional
    public void saveAll(List<OrderedItem> orderedItems) {
        orderedItemRepository.saveAllAndFlush(orderedItems);
    }

    @Override
    @Transactional
    public void update(Long id, boolean isReadyToServe) {
        OrderedItem orderedItem = findById(id).orElseThrow();
        orderedItem.setReadyToServe(isReadyToServe);
        orderedItemRepository.saveAndFlush(orderedItem);
        orderedItemRepository.refresh(orderedItem);
        List<Order> orders = orderRepository.findAllDineIn();
        messagingTemplate.convertAndSend("/topic/restaurant-orders", orders);
    }
}