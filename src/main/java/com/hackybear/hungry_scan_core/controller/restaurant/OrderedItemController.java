package com.hackybear.hungry_scan_core.controller.restaurant;


import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.OrderedItem;
import com.hackybear.hungry_scan_core.service.interfaces.OrderedItemService;
import com.hackybear.hungry_scan_core.utility.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant/ordered-items")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
public class OrderedItemController {

    private final OrderedItemService orderedItemService;
    private final ResponseHelper responseHelper;

    public OrderedItemController(OrderedItemService orderedItemService, ResponseHelper responseHelper) {
        this.orderedItemService = orderedItemService;
        this.responseHelper = responseHelper;
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @GetMapping
    public ResponseEntity<List<OrderedItem>> getAll() {
        return ResponseEntity.ok(orderedItemService.findAll());
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @GetMapping("/drinks")
    public ResponseEntity<List<OrderedItem>> getAllDrinks() {
        return ResponseEntity.ok(orderedItemService.findAllDrinks());
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping("/show")
    public ResponseEntity<?> show(@RequestBody Long id) {
        return responseHelper.getObjectAndBuildResponse(id, orderedItemService::findById);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
