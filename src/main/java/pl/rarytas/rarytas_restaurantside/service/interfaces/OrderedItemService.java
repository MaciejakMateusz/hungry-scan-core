package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface OrderedItemService {
    List<OrderedItem> findAll();

    OrderedItem findById(Long id) throws LocalizedException;

    void delete(OrderedItem orderedItem);

    void saveAll(List<OrderedItem> orderedItems);

    void toggleIsReadyToServe(Long id);
}
