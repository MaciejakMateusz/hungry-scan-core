package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;

import java.util.List;
import java.util.Optional;

public interface HistoryOrderRepository extends CustomRepository<HistoryOrder, Long> {
    @Query(value = "SELECT o FROM HistoryOrder o WHERE o.paid = false AND o.forTakeAway = false AND o.isResolved = false")
    List<HistoryOrder> findAllNotPaid();

    @Query(value = "SELECT o FROM HistoryOrder o WHERE o.forTakeAway = true AND o.isResolved = false ORDER BY o.id DESC")
    List<HistoryOrder> findAllTakeAway();

    @Query(value = "SELECT o FROM HistoryOrder o WHERE o.isResolved = true ORDER BY o.id DESC")
    List<HistoryOrder> findAllResolved();

    @Query(value = "SELECT * FROM history_orders WHERE is_resolved = true AND take_away = true LIMIT 50", nativeQuery = true)
    List<HistoryOrder> findAllResolvedTakeAwayLimit50();

    @Query(value = "SELECT * FROM history_orders WHERE table_id = :tableNumber ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<HistoryOrder> findNewestOrderByTableNumber(@Param("tableNumber") Integer tableNumber);

    boolean existsByRestaurantTable(RestaurantTable restaurantTable);

    @Query(value =
            "SELECT * FROM history_orders " +
                    "WHERE is_resolved = true " +
                    "AND take_away = :forTakeAway " +
                    "ORDER BY id DESC " +
                    "LIMIT :limit " +
                    "OFFSET :offset", nativeQuery = true)
    List<HistoryOrder> findFinalizedOrders(@Param("forTakeAway") boolean forTakeAway,
                                    @Param("limit") Integer limit,
                                    @Param("offset") Integer offset);

    @Query(value = "SELECT o FROM HistoryOrder o WHERE o.id = :id AND o.forTakeAway = :forTakeAway")
    Optional<HistoryOrder> findFinalizedById(@Param("id") Long id,
                                      @Param("forTakeAway") boolean forTakeAway);

    @Query(value = "SELECT o FROM HistoryOrder o WHERE o.orderTime LIKE %:date AND o.forTakeAway = :forTakeAway")
    List<HistoryOrder> findFinalizedByDate(@Param("date") String date,
                                    @Param("forTakeAway") boolean forTakeAway);


    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM HistoryOrder e WHERE e.id = :id AND e.forTakeAway = :forTakeAway AND e.isResolved = :isResolved")
    boolean existsByIdForTakeWayIsResolved(@Param("id") Long id,
                                           @Param("forTakeAway") boolean forTakeAway,
                                           @Param("isResolved") boolean isResolved);
}