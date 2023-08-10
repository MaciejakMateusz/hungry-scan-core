package pl.rarytas.rarytas_restaurantside.controller.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.repository.OrderedItemRepository;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/orderedItems")
public class OrderedItemRestController {
    private final OrderedItemRepository orderedItemRepository;

    public OrderedItemRestController(OrderedItemRepository orderedItemRepository) {
        this.orderedItemRepository = orderedItemRepository;
    }

    @GetMapping
    public List<OrderedItem> getAll() {
        return orderedItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public OrderedItem getById(@PathVariable Integer id) {
        return orderedItemRepository.findById(id).orElseThrow();
    }

//    @GetMapping("/order/{id}")
//    public List<OrderedItem> getByOrderId(@PathVariable Integer id) {
//        return orderedItemRepository.findByOrderId(id);
//    }

//    @PostMapping
//    public void saveItem(@RequestParam List<OrderedItem> orderedItems) {
//        orderedItemRepository.save(orderedItems);
//    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
