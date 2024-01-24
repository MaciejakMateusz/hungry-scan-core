package pl.rarytas.rarytas_restaurantside.repository.archive;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrderedItem;

public interface HistoryOrderedItemRepository extends JpaRepository<HistoryOrderedItem, Long> {
}