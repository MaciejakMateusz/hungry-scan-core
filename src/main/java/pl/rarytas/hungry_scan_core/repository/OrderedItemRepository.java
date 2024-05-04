package pl.rarytas.hungry_scan_core.repository;

import org.springframework.stereotype.Repository;
import pl.rarytas.hungry_scan_core.entity.OrderedItem;

@Repository
public interface OrderedItemRepository extends CustomRepository<OrderedItem, Long> {
}
