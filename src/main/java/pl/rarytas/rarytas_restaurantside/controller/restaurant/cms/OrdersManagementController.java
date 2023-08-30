package pl.rarytas.rarytas_restaurantside.controller.restaurant.cms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/restaurant/cms/orders")
public class OrdersManagementController {
    private final OrderService orderService;

    public OrdersManagementController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String orders() {
        return "restaurant/cms/orders/list";
    }

    @ModelAttribute(name = "orders")
    private List<Order> getOrders() {
        return orderService.findAllByResolvedIsTrue();
        // Wstępne rozwiązanie, do wykonania paginacja z wyszukiwaniem po dacie zamówienia
    }
}
