package pl.rarytas.rarytas_restaurantside.controller.restaurant;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/ordered-items")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class OrderedItemController {

    private final OrderedItemService orderedItemService;
    private final ResponseHelper responseHelper;

    public OrderedItemController(OrderedItemService orderedItemService, ResponseHelper responseHelper) {
        this.orderedItemService = orderedItemService;
        this.responseHelper = responseHelper;
    }

    @GetMapping
    public ResponseEntity<List<OrderedItem>> getAll() {
        return ResponseEntity.ok(orderedItemService.findAll());
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderedItemService::findById);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveAll(@RequestBody List<OrderedItem> orderedItems) {
        return responseHelper.buildResponse(orderedItems, orderedItemService::saveAll);
    }

    @PatchMapping("/toggle-item")
    public ResponseEntity<Map<String, Object>> toggleReadyToServe(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderedItemService::toggleIsReadyToServe);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "POST, PATCH, GET");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
