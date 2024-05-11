package pl.rarytas.hungry_scan_core.service.history.interfaces;

import pl.rarytas.hungry_scan_core.entity.history.HistoryOrderedItem;

import java.util.List;
import java.util.Optional;

public interface HistoryOrderedItemService {
    List<HistoryOrderedItem> findAll();

    Optional<HistoryOrderedItem> findById(Long id);
}
