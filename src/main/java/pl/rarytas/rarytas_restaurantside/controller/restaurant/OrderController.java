package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.utility.Constants;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/orders")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
public class OrderController {


    private final OrderService orderService;
    private final ResponseHelper responseHelper;

    public OrderController(OrderService orderService, ResponseHelper responseHelper) {
        this.orderService = orderService;
        this.responseHelper = responseHelper;
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @GetMapping("/take-away")
    public ResponseEntity<List<Order>> getAllTakeAwayOrders() {
        return ResponseEntity.ok(orderService.findAllTakeAway());
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @GetMapping("/dine-in")
    public ResponseEntity<List<Order>> getAllDineInOrders() {
        return ResponseEntity.ok(orderService.findAllDineIn());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/table-number")
    public ResponseEntity<Map<String, Object>> getByTableNumber(@RequestBody Integer number) {
        return responseHelper.getResponseEntity(number, orderService::findByTableNumber);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> getById(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderService::findById);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/take-away")
    public ResponseEntity<Map<String, Object>> saveTakeAwayOrder(@RequestBody Order order) {
        return responseHelper.buildResponse(order, orderService::saveTakeAway);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/dine-in")
    public ResponseEntity<Map<String, Object>> saveDineInOrder(@RequestBody Order order) {
        return responseHelper.buildResponse(order, orderService::save);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping
    public ResponseEntity<Map<String, Object>> orderMoreDishes(@RequestBody Order order) {
        return responseHelper.buildResponse(order, orderService::orderMoreDishes);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/tip")
    public ResponseEntity<Map<String, Object>> tip(@RequestParam("id") Long id,
                                                   @RequestParam("value") BigDecimal value) {
        return responseHelper.buildResponse(id, value, orderService::tip);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @PostMapping("/finalize-dine-in")
    public ResponseEntity<Map<String, Object>> finalizeDineInOrder(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderService::finish);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
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