package pl.rarytas.rarytas_restaurantside.controller.restaurant.orders;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
        return "restaurant/orders/dine-in";
    }

    @GetMapping("/take-away")
    public String takeAwayOrders() {
        return "restaurant/orders/take-away";
    }

    @GetMapping("/finalized")
    public String finalizedOrders() {
        return "restaurant/orders/finalized-orders";
    }

    @PostMapping("/finalize-dineIn")
    public String finalizeDineInOrder(@RequestParam Integer id,
                                      @RequestParam boolean paid,
                                      @RequestParam boolean isResolved) {
        orderService.finish(id, paid, isResolved);
        return "restaurant/orders/dine-in";
    }

    @PostMapping("/finalize-takeAway")
    public String finalizeTakeAwayOrder(@RequestParam Integer id,
                                        @RequestParam boolean paid,
                                        @RequestParam boolean isResolved) {
        orderService.finishTakeAway(id, paid, isResolved);
        return "restaurant/orders/take-away";
    }

    @ModelAttribute(name = "orders")
    private List<Order> getOrders() {
        return orderService.findAllPaidLimit50();
        // Wstępne rozwiązanie, do wykonania paginacja z wyszukiwaniem po dacie zamówienia
    }

}
