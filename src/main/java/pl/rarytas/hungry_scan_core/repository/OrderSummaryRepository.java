package pl.rarytas.hungry_scan_core.repository;

import org.springframework.stereotype.Repository;
import pl.rarytas.hungry_scan_core.entity.OrderSummary;
import pl.rarytas.hungry_scan_core.entity.RestaurantTable;

import java.util.Optional;

@Repository
public interface OrderSummaryRepository extends CustomRepository<OrderSummary, Long> {

    Optional<OrderSummary> findFirstByRestaurantTableId(Integer tableNumber);

    boolean existsByRestaurantTable(RestaurantTable restaurantTable);
}