package pl.rarytas.rarytas_restaurantside.service.archive.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrderedItem;

import java.util.List;
import java.util.Optional;

public interface HistoryOrderedItemServiceInterface {
    List<HistoryOrderedItem> findAll();
    Optional<HistoryOrderedItem> findById(Long id);
}
