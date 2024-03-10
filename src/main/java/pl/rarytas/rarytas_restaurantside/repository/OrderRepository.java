package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CustomRepository<Order, Long> {

    @Query(value = "SELECT o FROM Order o WHERE o.forTakeAway = false")
    List<Order> findAllDineIn();

    @Query(value = "SELECT o FROM Order o WHERE o.forTakeAway = true")
    List<Order> findAllTakeAway();

    @Query(value = "SELECT * FROM orders WHERE table_id = :tableNumber ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Order> findNewestOrderByTableNumber(@Param("tableNumber") Integer tableNumber);

    boolean existsByRestaurantTable(RestaurantTable restaurantTable);
}