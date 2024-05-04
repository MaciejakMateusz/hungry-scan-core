package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.OrderedItem;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface OrderedItemService {
    List<OrderedItem> findAll();

    OrderedItem findById(Long id) throws LocalizedException;

    void saveAll(List<OrderedItem> orderedItems);

    void toggleIsReadyToServe(Long id);
}
