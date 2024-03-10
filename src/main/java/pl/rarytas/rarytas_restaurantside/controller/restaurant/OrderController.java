package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/orders")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class OrderController {

    private final OrderService orderService;
    private final ResponseHelper responseHelper;

    public OrderController(OrderService orderService, ResponseHelper responseHelper) {
        this.orderService = orderService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllNotPaid() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/takeAway")
    public ResponseEntity<List<Order>> getAllTakeAway() {
        return ResponseEntity.ok(orderService.findAllTakeAway());
    }

    @GetMapping("/{number}")
    public ResponseEntity<Order> getByTableNumber(@PathVariable Integer number) {
        return ResponseEntity.ok(orderService.findByTableNumber(number).orElseThrow());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return responseHelper.getResponseEntity(id, orderService::findById);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveOrder(@RequestBody Order order) throws LocalizedException {
        if (order.isForTakeAway()) {
            orderService.saveTakeAway(order);
            return ResponseEntity.ok(new HashMap<>());
        }
        orderService.save(order);
        return ResponseEntity.ok(new HashMap<>());
    }

    @PatchMapping
    public ResponseEntity<Map<String, Object>> orderMoreDishes(@RequestBody Order order) throws LocalizedException {
        orderService.orderMoreDishes(order);
        return ResponseEntity.ok(new HashMap<>());
    }

    @PatchMapping("/request-bill")
    public ResponseEntity<Map<String, Object>> requestBill(@RequestBody Order order) throws LocalizedException {
        orderService.requestBill(order);
        return ResponseEntity.ok(new HashMap<>());
    }

    @PatchMapping("/call-waiter")
    public ResponseEntity<Map<String, Object>> callWaiter(@RequestBody Order order) throws LocalizedException {
        orderService.callWaiter(order);
        return ResponseEntity.ok(new HashMap<>());
    }

    @PostMapping("/finalize-dineIn")
    public ResponseEntity<Map<String, Object>> finalizeDineInOrder(@RequestParam Long id,
                                                                   @RequestParam boolean paid,
                                                                   @RequestParam boolean isResolved) throws LocalizedException {
        orderService.finish(id, paid, isResolved);
        return ResponseEntity.ok(new HashMap<>());
    }

    @PostMapping("/finalize-takeAway")
    public String finalizeTakeAwayOrder(@RequestParam Long id,
                                        @RequestParam boolean paid,
                                        @RequestParam boolean isResolved) throws LocalizedException {
        orderService.finishTakeAway(id, paid, isResolved);
        return "redirect:/restaurant/orders/take-away";
    }

    @PostMapping("/resolve-call")
    public String resolveWaiterCall(@RequestParam Long id) throws LocalizedException {
        orderService.resolveWaiterCall(id);
        return "redirect:/restaurant";
    }

}
