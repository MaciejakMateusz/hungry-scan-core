package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.repository.OrderedItemRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderedItemServiceInterface;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderedItemService implements OrderedItemServiceInterface {
    private final OrderedItemRepository orderedItemRepository;

    public OrderedItemService(OrderedItemRepository orderedItemRepository) {
        this.orderedItemRepository = orderedItemRepository;
    }

    @Override
    public List<OrderedItem> findAll() {
        return orderedItemRepository.findAll();
    }

    @Override
    public Optional<OrderedItem> findById(Integer id) {
        return orderedItemRepository.findById(id);
    }
}