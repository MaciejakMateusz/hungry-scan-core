package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/restaurant/orders")
public class OrdersController {

    private final OrderService orderService;

    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String dineInOrders() {
        return "restaurant/dine-in";
    }

    @GetMapping("/take-away")
    public String takeAwayOrders() {
        return "restaurant/take-away";
    }

    @GetMapping("/finalized")
    public String finalizedOrders() {
        return "restaurant/finalized-orders";
    }

    @ModelAttribute(name = "orders")
    private List<Order> getOrders() {
        return orderService.findAllPaidLimit50();
        // Wstępne rozwiązanie, do wykonania paginacja z wyszukiwaniem po dacie zamówienia
    }

}