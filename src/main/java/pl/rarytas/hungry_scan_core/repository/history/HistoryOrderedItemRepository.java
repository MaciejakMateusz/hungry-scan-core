package pl.rarytas.hungry_scan_core.repository.history;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrderedItem;

public interface HistoryOrderedItemRepository extends JpaRepository<HistoryOrderedItem, Long> {
}