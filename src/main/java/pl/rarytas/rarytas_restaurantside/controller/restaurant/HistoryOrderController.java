package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.utility.TriFunction;

import java.time.LocalDate;
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
    public ResponseEntity<List<HistoryOrder>> getAllDineInHistoryOrders(@RequestBody Map<String, Integer> requestBody) {
        Integer pageNumber = requestBody.get("pageNumber");
        Integer pageSize = requestBody.get("pageSize");
        List<HistoryOrder> orders = historyOrderService
                .findAllDineIn(PageRequest.of(pageNumber, pageSize));
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/take-away")
    public ResponseEntity<List<HistoryOrder>> getAllForTakeAwayHistoryOrders(@RequestBody Map<String, Object> requestBody) {
        int pageNumber = (int) requestBody.get("pageNumber");
        int pageSize = (int) requestBody.get("pageSize");
        List<HistoryOrder> orders = historyOrderService
                .findAllForTakeAway(PageRequest.of(pageNumber, pageSize));
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(historyOrderService.countAll());
    }

    @PostMapping("/dine-in/date")
    public ResponseEntity<List<HistoryOrder>> getDineInByDate(@RequestBody Map<String, Object> requestBody) {
        return getFilteredHistoryOrders(requestBody, historyOrderService::findDineInByDate);
    }

    @PostMapping("/take-away/date")
    public ResponseEntity<List<HistoryOrder>> getTakeAwayByDate(@RequestBody Map<String, Object> requestBody) {
        return getFilteredHistoryOrders(requestBody, historyOrderService::findTakeAwayByDate);
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, historyOrderService::findById);
    }

    private ResponseEntity<List<HistoryOrder>> getFilteredHistoryOrders(Map<String, Object> requestBody,
                                                                        TriFunction<
                                                                                Pageable,
                                                                                LocalDate,
                                                                                LocalDate,
                                                                                List<HistoryOrder>> getByDate) {
        Integer pageNumber = (Integer) requestBody.get("pageNumber");
        Integer pageSize = (Integer) requestBody.get("pageSize");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        LocalDate startDate = LocalDate.parse((CharSequence) requestBody.get("startDate"));
        LocalDate endDate = LocalDate.parse((CharSequence) requestBody.get("endDate"));
        List<HistoryOrder> orders = getByDate.apply(pageable, startDate, endDate);
        return ResponseEntity.ok(orders);
    }
}
