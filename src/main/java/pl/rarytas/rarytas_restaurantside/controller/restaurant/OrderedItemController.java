package pl.rarytas.rarytas_restaurantside.controller.restaurant;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.controller.ResponseHelper;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;
import pl.rarytas.rarytas_restaurantside.utility.Constants;

import java.util.List;
import java.util.Map;

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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderedItemService::findById);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveAll(@RequestBody List<OrderedItem> orderedItems) {
        return responseHelper.buildResponse(orderedItems, orderedItemService::saveAll);
    }

    @PreAuthorize(Constants.ROLES_EXCEPT_CUSTOMER)
    @PatchMapping("/toggle-item")
    public ResponseEntity<Map<String, Object>> toggleReadyToServe(@RequestBody Long id) {
        return responseHelper.getResponseEntity(id, orderedItemService::toggleIsReadyToServe);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
