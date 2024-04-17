package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.OrderSummary;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.util.Optional;

@Repository
public interface OrderSummaryRepository extends CustomRepository<OrderSummary, Long> {

    Optional<OrderSummary> findFirstByRestaurantTableId(Integer tableNumber);

    boolean existsByRestaurantTable(RestaurantTable restaurantTable);
}