package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/take-away")
    public ResponseEntity<List<Order>> getAllTakeAwayOrders() {
        return ResponseEntity.ok(orderService.findAllTakeAway());
    }

    @GetMapping("/dine-in")
    public ResponseEntity<List<Order>> getAllDineInOrders() {
        return ResponseEntity.ok(orderService.findAllDineIn());
    }

    @PostMapping("/table-number")
    public ResponseEntity<Map<String, Object>> getByTableNumber(@RequestBody Integer number) {
        return responseHelper.getResponseEntity(number, orderService::findByTableNumber);
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> getById(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderService::findById);
    }

    @PostMapping("/take-away")
    public ResponseEntity<Map<String, Object>> saveTakeAwayOrder(@RequestBody Order order) {
        orderService.saveTakeAway(order);
        return ResponseEntity.ok(new HashMap<>());
    }

    @PostMapping("/dine-in")
    public ResponseEntity<Map<String, Object>> saveDineInOrder(@RequestBody Order order) throws LocalizedException {
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

    @PatchMapping("/resolve-call")
    public ResponseEntity<Map<String, Object>> resolveWaiterCall(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderService::resolveWaiterCall);
    }

    @PostMapping("/finalize-dine-in")
    public ResponseEntity<Map<String, Object>> finalizeDineInOrder(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderService::finish);
    }

    @PostMapping("/finalize-take-away")
    public ResponseEntity<Map<String, Object>> finalizeTakeAwayOrder(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderService::finishTakeAway);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}