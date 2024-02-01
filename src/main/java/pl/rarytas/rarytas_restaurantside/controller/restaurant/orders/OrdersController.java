package pl.rarytas.rarytas_restaurantside.controller.restaurant.orders;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;

@Controller
@RequestMapping("/restaurant/orders")
public class OrdersController {

    private final OrderService orderService;

    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/take-away")
    public String takeAwayOrders() {
        return "restaurant/orders/take-away";
    }

    @GetMapping("/finalized")
    public String finalizedOrders() {
        return "restaurant/orders/history-dineIn";
    }

    @GetMapping("/finalized/take-away")
    public String finalizedTakeAwayOrders() {
        return "restaurant/orders/history-takeAway";
    }

    @PostMapping("/finalize-dineIn")
    public String finalizeDineInOrder(@RequestParam Integer id,
                                      @RequestParam boolean paid,
                                      @RequestParam boolean isResolved) throws LocalizedException {
        orderService.finish(id, paid, isResolved);
        return "redirect:/restaurant";
    }

    @PostMapping("/finalize-takeAway")
    public String finalizeTakeAwayOrder(@RequestParam Integer id,
                                        @RequestParam boolean paid,
                                        @RequestParam boolean isResolved) throws LocalizedException {
        orderService.finishTakeAway(id, paid, isResolved);
        return "redirect:/restaurant/orders/take-away";
    }

    @PostMapping("/resolve-call")
    public String resolveWaiterCall(@RequestParam Integer id) throws LocalizedException {
        orderService.resolveWaiterCall(id);
        return "redirect:/restaurant";
    }

}
