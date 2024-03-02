package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;

import java.util.List;
import java.util.Optional;

public interface OrderedItemService {
    List<OrderedItem> findAll();

    Optional<OrderedItem> findById(Long id);

    void delete(OrderedItem orderedItem);

    void saveAll(List<OrderedItem> orderedItems);

    void update(Long id, boolean isReadyToServe);
}
