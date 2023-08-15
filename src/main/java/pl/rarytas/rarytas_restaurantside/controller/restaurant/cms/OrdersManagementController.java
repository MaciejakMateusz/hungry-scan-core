package pl.rarytas.rarytas_restaurantside.controller.restaurant.cms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;

import java.util.List;

@Controller
@RequestMapping("/restaurant/cms/orders")
public class OrdersManagementController {
    private final OrderRepository orderRepository;

    public OrdersManagementController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public String orders() {
        return "/restaurant/cms/orders/list";
    }

    @ModelAttribute(name = "orders")
    private List<Order> getOrders() {
        return orderRepository.findAllPaidLimit50();
        // Wstępne rozwiązanie, do wykonania paginacja z wyszukiwaniem po dacie zamówienia
    }
}
