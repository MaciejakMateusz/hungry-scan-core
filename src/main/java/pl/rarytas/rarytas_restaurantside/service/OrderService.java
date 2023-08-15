package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderServiceInterface;

import java.util.List;
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
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public void saveOrPatch(Order order) {
        orderRepository.save(order);
        orderRepository.refresh(order);
        messagingTemplate.convertAndSend("/topic/restaurant-order", findAllNotPaid());
    }

}
