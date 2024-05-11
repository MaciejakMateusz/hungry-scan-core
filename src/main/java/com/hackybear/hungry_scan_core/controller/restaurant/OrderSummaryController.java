package com.hackybear.hungry_scan_core.controller.restaurant;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.service.interfaces.OrderSummaryService;
import com.hackybear.hungry_scan_core.utility.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/summaries")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
public class OrderSummaryController {


    private final OrderSummaryService orderSummaryService;
    private final ResponseHelper responseHelper;

    public OrderSummaryController(OrderSummaryService orderSummaryService, ResponseHelper responseHelper) {
        this.orderSummaryService = orderSummaryService;
        this.responseHelper = responseHelper;
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @GetMapping
    public ResponseEntity<List<OrderSummary>> getAllSummaries() {
        return ResponseEntity.ok(orderSummaryService.findAll());
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping("/table-number")
    public ResponseEntity<Map<String, Object>> getByTableNumber(@RequestBody Integer number) {
        return responseHelper.getResponseEntity(number, orderSummaryService::findByTableNumber);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> getById(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderSummaryService::findById);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PatchMapping("/tip")
    public ResponseEntity<Map<String, Object>> tip(@RequestParam("id") Long id,
                                                   @RequestParam("value") BigDecimal value) {
        return responseHelper.buildResponse(id, value, orderSummaryService::tip);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @PostMapping("/finalize-dine-in")
    public ResponseEntity<Map<String, Object>> finalizeDineInOrder(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderSummaryService::finish);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}