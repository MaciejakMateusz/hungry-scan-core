package pl.rarytas.hungry_scan_core.controller.restaurant.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.hungry_scan_core.controller.ResponseHelper;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrder;
import pl.rarytas.hungry_scan_core.service.history.interfaces.HistoryOrderService;
import pl.rarytas.hungry_scan_core.utility.Constants;

import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/history-orders")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
public class HistoryOrderController {

    private final HistoryOrderService historyOrderService;
    private final ResponseHelper responseHelper;

    public HistoryOrderController(HistoryOrderService historyOrderService, ResponseHelper responseHelper) {
        this.historyOrderService = historyOrderService;
        this.responseHelper = responseHelper;
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @PostMapping("/dine-in")
    public ResponseEntity<Page<HistoryOrder>> getAllDineInHistoryOrders(@RequestBody Map<String, Integer> requestBody) {
        Integer pageNumber = requestBody.get("pageNumber");
        Integer pageSize = requestBody.get("pageSize");
        Page<HistoryOrder> orders = historyOrderService
                .findAllDineIn(PageRequest.of(pageNumber, pageSize));
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @PostMapping("/take-away")
    public ResponseEntity<Page<HistoryOrder>> getAllForTakeAwayHistoryOrders(@RequestBody Map<String, Object> requestBody) {
        int pageNumber = (int) requestBody.get("pageNumber");
        int pageSize = (int) requestBody.get("pageSize");
        Page<HistoryOrder> orders = historyOrderService
                .findAllForTakeAway(PageRequest.of(pageNumber, pageSize));
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @GetMapping("/count")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(historyOrderService.countAll());
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @PostMapping("/dine-in/date")
    public ResponseEntity<Page<HistoryOrder>> getDineInByDate(@RequestBody Map<String, Object> requestBody) {
        return responseHelper.getEntitiesByDateRange(requestBody, historyOrderService::findDineInByDate);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @PostMapping("/take-away/date")
    public ResponseEntity<Page<HistoryOrder>> getTakeAwayByDate(@RequestBody Map<String, Object> requestBody) {
        return responseHelper.getEntitiesByDateRange(requestBody, historyOrderService::findTakeAwayByDate);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, historyOrderService::findById);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}