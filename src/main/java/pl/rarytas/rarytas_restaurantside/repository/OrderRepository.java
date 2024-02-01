package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CustomRepository<Order, Integer> {

    @Query(value = "SELECT o FROM Order o WHERE o.paid = false AND o.forTakeAway = false AND o.isResolved = false")
    List<Order> findAllNotPaid();

    @Query(value = "SELECT o FROM Order o WHERE o.forTakeAway = true AND o.isResolved = false ORDER BY o.id DESC")
    List<Order> findAllTakeAway();

    @Query(value = "SELECT o FROM Order o WHERE o.isResolved = true ORDER BY o.id DESC")
    List<Order> findAllResolved();

    @Query(value = "SELECT * FROM orders WHERE table_id = :tableNumber ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Order> findNewestOrderByTableNumber(@Param("tableNumber") Integer tableNumber);

    boolean existsByRestaurantTable(RestaurantTable restaurantTable);

    @Query(value = "SELECT o FROM Order o WHERE o.id = :id AND o.forTakeAway = :forTakeAway")
    Optional<Order> findFinalizedById(@Param("id") Integer id,
                                      @Param("forTakeAway") boolean forTakeAway);
}