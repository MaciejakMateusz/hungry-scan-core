package pl.rarytas.rarytas_restaurantside.repository.history;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrderedItem;

public interface HistoryOrderedItemRepository extends JpaRepository<HistoryOrderedItem, Long> {
}