package com.hackybear.hungry_scan_core.controller.restaurant.history;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.history.HistoryBooking;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryBookingService;
import com.hackybear.hungry_scan_core.utility.Fields;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/history-bookings")
@PreAuthorize(Fields.ROLES_EXCEPT_CUSTOMER)
public class HistoryBookingController {

    private final HistoryBookingService historyBookingService;
    private final ResponseHelper responseHelper;

    public HistoryBookingController(HistoryBookingService historyBookingService, ResponseHelper responseHelper) {
        this.historyBookingService = historyBookingService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, historyBookingService::findById);
    }

    @PostMapping("/date")
    public ResponseEntity<Page<HistoryBooking>> getByDate(@RequestBody Map<String, Object> requestBody) {
        return responseHelper.getEntitiesByDateRange(requestBody, historyBookingService::findAllByDateBetween);
    }

    @GetMapping("/count-all")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(historyBookingService.countAll());
    }

    @PostMapping("/count-dates")
    public ResponseEntity<Long> countByDateBetween(@RequestBody Map<String, LocalDate> requestBody) {
        LocalDate dateFrom = requestBody.get("dateFrom");
        LocalDate dateTo = requestBody.get("dateTo");
        return ResponseEntity.ok(historyBookingService.countByDateBetween(dateFrom, dateTo));
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}