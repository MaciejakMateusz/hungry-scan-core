package com.hackybear.hungry_scan_core.controller.restaurant.history;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.history.HistoryOrderSummary;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryOrderSummaryService;
import com.hackybear.hungry_scan_core.utility.Fields;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/history-summaries")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@RequiredArgsConstructor
public class HistoryOrderSummaryController {

    private final HistoryOrderSummaryService historyOrderSummaryService;
    private final ResponseHelper responseHelper;

    @PreAuthorize(Fields.ROLES_EXCEPT_CUSTOMER)
    @GetMapping("/count")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(historyOrderSummaryService.countAll());
    }

    @PreAuthorize(Fields.ROLES_EXCEPT_CUSTOMER)
    @PostMapping("/date")
    public ResponseEntity<Page<HistoryOrderSummary>> getByDateBetween(@RequestBody Map<String, Object> requestBody) {
        return responseHelper.getEntitiesByDateRange(requestBody, historyOrderSummaryService::findByDateBetween);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, historyOrderSummaryService::findById);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
