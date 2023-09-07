package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CustomRepository<Order, Integer> {
    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE is_paid = false AND take_away = false AND is_resolved = false", nativeQuery = true)
    List<Order> findAllNotPaid();

    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE take_away = true AND is_resolved = false ORDER BY id DESC", nativeQuery = true)
    List<Order> findAllTakeAway();

    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE is_resolved = true ORDER BY id DESC", nativeQuery = true)
    List<Order> findAllResolved();

    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE is_resolved = true AND take_away = true LIMIT 50", nativeQuery = true)
    List<Order> findAllResolvedTakeAwayLimit50();

    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE table_id = :tableNumber ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Order> findNewestOrderByTableNumber(@Param("tableNumber") Integer tableNumber);

    boolean existsByRestaurantTable(RestaurantTable restaurantTable);

    @Query(value =
            "SELECT * FROM rarytas_testing.orders " +
                    "WHERE is_resolved = true " +
                    "AND take_away = :forTakeAway " +
                    "ORDER BY id DESC " +
                    "LIMIT :limit " +
                    "OFFSET :offset", nativeQuery = true)
    List<Order> findFinalizedOrders(@Param("forTakeAway") boolean forTakeAway,
                                    @Param("limit") Integer limit,
                                    @Param("offset") Integer offset);

    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE id = :id AND take_away = :forTakeAway", nativeQuery = true)
    Optional<Order> findFinalizedById(@Param("id") Integer id,
                                      @Param("forTakeAway") boolean forTakeAway);

    @Query(value = "SELECT o FROM Order o WHERE o.orderTime LIKE %:date AND o.forTakeAway = :forTakeAway")
    List<Order> findFinalizedByDate(@Param("date") String date,
                                    @Param("forTakeAway") boolean forTakeAway);

}