package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;

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
        return responseHelper.buildResponse(order, orderService::saveTakeAway);
    }

    @PostMapping("/dine-in")
    public ResponseEntity<Map<String, Object>> saveDineInOrder(@RequestBody Order order) {
        return responseHelper.buildResponse(order, orderService::save);
    }

    @PatchMapping
    public ResponseEntity<Map<String, Object>> orderMoreDishes(@RequestBody Order order) {
        return responseHelper.buildResponse(order, orderService::orderMoreDishes);
    }

    @PatchMapping("/request-bill")
    public ResponseEntity<Map<String, Object>> requestBill(@RequestParam Long id,
                                                           @RequestParam String paymentMethod) {
        return responseHelper.buildResponse(id, paymentMethod, orderService::requestBill);
    }

    @PatchMapping("/call-waiter")
    public ResponseEntity<Map<String, Object>> callWaiter(@RequestBody Long id) {
        return responseHelper.buildResponse(id, orderService::callWaiter);
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