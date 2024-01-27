package pl.rarytas.rarytas_restaurantside.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:8080")
public class OrderRestController {

    private final OrderService orderService;
    private final HistoryOrderService historyOrderService;

    public OrderRestController(OrderService orderService, HistoryOrderService historyOrderService) {
        this.orderService = orderService;
        this.historyOrderService = historyOrderService;
    }

    @GetMapping
    public List<Order> getAllNotPaid() {
        return orderService.findAllNotPaid();
    }

    @GetMapping("/resolved")
    public List<HistoryOrder> getAllResolved() {
        return historyOrderService.findAllByResolvedIsTrue();
    }

    @PostMapping("/finalized")
    public List<HistoryOrder> getFinalizedOrders(@RequestBody Map<String, Object> requestBody) {
        boolean forTakeAway = (boolean) requestBody.get("forTakeAway");
        int limit = (int) requestBody.get("limit");
        int offset = (int) requestBody.get("offset");

        return historyOrderService.findAllFinalized(forTakeAway, limit, offset);
    }

    @GetMapping("/finalized/id/{id}/{forTakeAway}")
    public HistoryOrder getFinalizedById(@PathVariable Long id,
                                  @PathVariable boolean forTakeAway) {
        if (historyOrderService.existsByIdAndForTakeAwayAndResolved(id, forTakeAway)) {
            return historyOrderService.findFinalizedById(id, forTakeAway).orElseThrow();
        } else {
            return null;
        }
    }

    @GetMapping("/finalized/date/{date}/{forTakeAway}")
    public List<HistoryOrder> getFinalizedByDate(@PathVariable String date,
                                          @PathVariable boolean forTakeAway) {
        return historyOrderService.findFinalizedByDate(date, forTakeAway);
    }

    @GetMapping("/resolved/take-away")
    public List<HistoryOrder> getAllResolvedTakeAwayLimit50() {
        return historyOrderService.findAllResolvedTakeAwayLimit50();
    }

    @GetMapping("/takeAway")
    public List<Order> getAllTakeAway() {
        return orderService.findAllTakeAway();
    }

    @GetMapping("/{number}")
    public Order getByTableNumber(@PathVariable Integer number) {
        return orderService.findByTableNumber(number).orElseThrow();
    }

    @GetMapping("/id/{id}")
    public Order getById(@PathVariable Integer id) {
        return orderService.findById(id).orElseThrow();
    }

    @PostMapping
    public void saveOrder(@RequestBody Order order) {
        if (order.isForTakeAway()) {
            orderService.saveTakeAway(order);
            return;
        }
        orderService.save(order);
    }

    @PatchMapping("/request-bill")
    public void requestBill(@RequestBody Order order) {
        orderService.requestBill(order);
    }

    @PatchMapping("/call-waiter")
    public void callWaiter(@RequestBody Order order) {
        orderService.callWaiter(order);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}

