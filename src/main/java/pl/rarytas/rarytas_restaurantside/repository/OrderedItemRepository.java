package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;

public interface OrderedItemRepository extends JpaRepository<OrderedItem, Long> {
}
