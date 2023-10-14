package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;

import java.util.List;
import java.util.Optional;

public interface OrderedItemServiceInterface {
    List<OrderedItem> findAll();
    Optional<OrderedItem> findById(Integer id);
}
