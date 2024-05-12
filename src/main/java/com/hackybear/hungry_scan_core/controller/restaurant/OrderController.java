package com.hackybear.hungry_scan_core.controller.restaurant;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.service.interfaces.OrderService;
import com.hackybear.hungry_scan_core.utility.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        return responseHelper.getObjectAndBuildResponse(tableId, orderService::findByTable);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping(value = "/dine-in")
    public ResponseEntity<?> saveDineInOrder(@RequestBody Order order) {
        return responseHelper.getObjectAndBuildResponse(order, orderService::saveDineIn);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}