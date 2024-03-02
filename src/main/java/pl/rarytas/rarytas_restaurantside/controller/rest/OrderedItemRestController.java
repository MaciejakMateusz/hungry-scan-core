package pl.rarytas.rarytas_restaurantside.controller.rest;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;

import java.util.List;

@RestController
@RequestMapping("/api/orderedItems")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class OrderedItemRestController {

    private final OrderedItemService orderedItemService;

    public OrderedItemRestController(OrderedItemService orderedItemService) {
        this.orderedItemService = orderedItemService;
    }

    @GetMapping
    public List<OrderedItem> getAll() {
        return orderedItemService.findAll();
    }

    @GetMapping("/{id}")
    public OrderedItem getById(@PathVariable Long id) {
        return orderedItemService.findById(id).orElseThrow();
    }

    @PostMapping
    public void saveAll(@RequestBody List<OrderedItem> orderedItems) {
        orderedItemService.saveAll(orderedItems);
    }

    @PatchMapping
    public void update(@RequestParam Long id, @RequestParam boolean isReadyToServe) {
        orderedItemService.update(id, isReadyToServe);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
