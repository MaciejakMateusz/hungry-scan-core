package pl.rarytas.rarytas_restaurantside.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/orders")
public class OrderRestController {
    private final OrderRepository orderRepository;

    public OrderRestController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Order getById(@PathVariable Integer id) {
        return orderRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public void saveOrder(@RequestBody Order order) {
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            orderedItem.setOrder(order);
        }
        orderRepository.save(order);
    }

    @PutMapping
    public void updateOrder(@RequestBody Order order) {
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            orderedItem.setOrder(order);
        }
        orderRepository.save(order);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PUT, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}

