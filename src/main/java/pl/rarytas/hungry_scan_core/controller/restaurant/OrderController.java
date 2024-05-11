package pl.rarytas.hungry_scan_core.controller.restaurant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.hungry_scan_core.controller.ResponseHelper;
import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.entity.OrderSummary;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.service.interfaces.OrderService;
import pl.rarytas.hungry_scan_core.utility.Constants;

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

    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> getById(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderService::findById);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping("/show/table")
    public ResponseEntity<?> getByTable(@RequestBody Integer tableId) {
        try {
            OrderSummary orderSummary = orderService.findByTable(tableId);
            return ResponseEntity.ok(orderSummary);
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping(value = "/dine-in")
    public ResponseEntity<?> saveDineInOrder(@RequestBody Order order) {
        try {
            OrderSummary orderSummary = orderService.saveDineIn(order);
            return ResponseEntity.ok(orderSummary);
        } catch (LocalizedException e) {
            return responseHelper.createErrorResponse(e);
        }
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}