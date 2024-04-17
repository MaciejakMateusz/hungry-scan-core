package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;

@Repository
public interface OrderedItemRepository extends CustomRepository<OrderedItem, Long> {
}
