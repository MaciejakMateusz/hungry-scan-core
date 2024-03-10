package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/history-orders")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class HistoryOrderController {

    private final HistoryOrderService historyOrderService;
    private final ResponseHelper responseHelper;

    public HistoryOrderController(HistoryOrderService historyOrderService, ResponseHelper responseHelper) {
        this.historyOrderService = historyOrderService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/dine-in")
    public ResponseEntity<Map<String, Object>> getAllDineIn(@RequestBody Map<String, Object> requestBody) {
        int pageNumber = (int) requestBody.get("limit");
        int pageSize = (int) requestBody.get("offset");
        Page<HistoryOrder> orders = historyOrderService
                .findAllDineIn(PageRequest.of(pageNumber, pageSize));
        Map<String, Object> params = Map.of("orders", orders);
        return ResponseEntity.ok(params);
    }

    @PostMapping("/take-away")
    public ResponseEntity<Map<String, Object>> getAllForTakeAway(@RequestBody Map<String, Object> requestBody) {
        int pageNumber = (int) requestBody.get("limit");
        int pageSize = (int) requestBody.get("offset");
        Page<HistoryOrder> orders = historyOrderService
                .findAllForTakeAway(PageRequest.of(pageNumber, pageSize));
        Map<String, Object> params = Map.of("orders", orders);
        return ResponseEntity.ok(params);
    }

    @GetMapping("/numberResolved")
    public ResponseEntity<Long> countAllResolved() {
        return ResponseEntity.ok(historyOrderService.countAll());
    }

    @GetMapping("/finalized/date/{date}/{forTakeAway}")
    public ResponseEntity<List<HistoryOrder>> getFinalizedByDate(@PathVariable String date,
                                                                 @PathVariable boolean forTakeAway) {
        return ResponseEntity.ok(historyOrderService.findFinalizedByDate(date, forTakeAway));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return responseHelper.getResponseEntity(id, historyOrderService::findById);
    }

}
