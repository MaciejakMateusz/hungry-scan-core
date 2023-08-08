package pl.rarytas.rarytas_restaurantside.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rarytas.rarytas_restaurantside.entity.Order;
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

//    @PostMapping
//    public void saveOrder(@RequestBody Integer tableId,
//                          @RequestBody BigDecimal totalAmount,
//                          @RequestBody List<OrderedItem> orderedItems) {
//        orderRepository.save(tableId, totalAmount, orderedItems);
//    }
}

