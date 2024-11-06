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


    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping("/pay")
    public ResponseEntity<?> requestPayment(@RequestBody OrderSummary orderSummary) {
        return responseHelper.getObjectAndBuildResponse(orderSummary, orderSummaryService::pay);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}