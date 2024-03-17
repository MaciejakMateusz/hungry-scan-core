package pl.rarytas.rarytas_restaurantside.service.history.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrderedItem;

import java.util.List;
import java.util.Optional;

public interface HistoryOrderedItemService {
    List<HistoryOrderedItem> findAll();

    Optional<HistoryOrderedItem> findById(Long id);
}
