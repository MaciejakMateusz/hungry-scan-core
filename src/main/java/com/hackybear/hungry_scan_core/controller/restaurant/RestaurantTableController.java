package com.hackybear.hungry_scan_core.controller.restaurant;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.BillSplitter;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import com.hackybear.hungry_scan_core.service.interfaces.BillSplitterService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import com.hackybear.hungry_scan_core.utility.Fields;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/tables")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;
    private final BillSplitterService billSplitterService;
    private final ResponseHelper responseHelper;

    public RestaurantTableController(RestaurantTableService restaurantTableService, BillSplitterService billSplitterService,
                                     ResponseHelper responseHelper) {
        this.restaurantTableService = restaurantTableService;
        this.billSplitterService = billSplitterService;
        this.responseHelper = responseHelper;
    }

    @PreAuthorize(Fields.ROLES_EXCEPT_CUSTOMER)
    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getAll() {
        return ResponseEntity.ok(restaurantTableService.findAll());
    }

    @PreAuthorize(Fields.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, restaurantTableService::findById);
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<RestaurantTable> add() {
        return ResponseEntity.ok(new RestaurantTable());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> save(@RequestBody @Valid RestaurantTable restaurantTable, BindingResult br) {
        return responseHelper.buildResponse(restaurantTable, br, restaurantTableService::createNew);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> delete(@RequestBody Long id) {
        return responseHelper.buildResponse(id, restaurantTableService::delete);
    }

    @PreAuthorize(Fields.ROLES_EXCEPT_CUSTOMER)
    @PatchMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleActivation(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, restaurantTableService::toggleActivation);
    }

    @PreAuthorize(Fields.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PatchMapping("/request-bill")
    public ResponseEntity<Map<String, Object>> requestBill(@RequestParam("id") Long id,
                                                           @RequestParam("value") PaymentMethod paymentMethod) {
        return responseHelper.buildResponse(id, paymentMethod, restaurantTableService::requestBill);
    }

    @PreAuthorize(Fields.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PatchMapping("/split-bill")
    public ResponseEntity<?> splitBill(@RequestBody @Valid BillSplitter billSplitter, BindingResult br) {
        return responseHelper.buildResponse(billSplitter, br, billSplitterService::splitBill);
    }

    @PreAuthorize(Fields.ROLES_EXCEPT_READONLY_CUSTOMER)
    @PatchMapping("/call-waiter")
    public ResponseEntity<Map<String, Object>> callWaiter(@RequestBody Long id) {
        return responseHelper.buildResponse(id, restaurantTableService::callWaiter);
    }

    @PreAuthorize(Fields.ROLES_EXCEPT_CUSTOMER)
    @PatchMapping("/resolve-call")
    public ResponseEntity<Map<String, Object>> resolveWaiterCall(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, restaurantTableService::resolveWaiterCall);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}