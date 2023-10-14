package pl.rarytas.rarytas_restaurantside.controller.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/orderedItems")
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
    public OrderedItem getById(@PathVariable Integer id) {
        return orderedItemService.findById(id).orElseThrow();
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
