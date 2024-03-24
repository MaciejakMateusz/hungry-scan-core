package pl.rarytas.rarytas_restaurantside.controller.restaurant.history;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.Feedback;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderService;

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
    public ResponseEntity<Page<HistoryOrder>> getAllDineInHistoryOrders(@RequestBody Map<String, Integer> requestBody) {
        Integer pageNumber = requestBody.get("pageNumber");
        Integer pageSize = requestBody.get("pageSize");
        Page<HistoryOrder> orders = historyOrderService
                .findAllDineIn(PageRequest.of(pageNumber, pageSize));
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/take-away")
    public ResponseEntity<Page<HistoryOrder>> getAllForTakeAwayHistoryOrders(@RequestBody Map<String, Object> requestBody) {
        int pageNumber = (int) requestBody.get("pageNumber");
        int pageSize = (int) requestBody.get("pageSize");
        Page<HistoryOrder> orders = historyOrderService
                .findAllForTakeAway(PageRequest.of(pageNumber, pageSize));
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAll() {
        return ResponseEntity.ok(historyOrderService.countAll());
    }

    @PostMapping("/dine-in/date")
    public ResponseEntity<Page<HistoryOrder>> getDineInByDate(@RequestBody Map<String, Object> requestBody) {
        return responseHelper.getEntitiesByDateRange(requestBody, historyOrderService::findDineInByDate);
    }

    @PostMapping("/take-away/date")
    public ResponseEntity<Page<HistoryOrder>> getTakeAwayByDate(@RequestBody Map<String, Object> requestBody) {
        return responseHelper.getEntitiesByDateRange(requestBody, historyOrderService::findTakeAwayByDate);
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, historyOrderService::findById);
    }

    @PatchMapping("/feedback")
    public ResponseEntity<?> feedback(@RequestBody @Valid Feedback feedback, BindingResult br) {
        return responseHelper.buildResponse(feedback, br ,historyOrderService::leaveFeedback);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
