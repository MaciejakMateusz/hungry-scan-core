package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.Query;
import pl.rarytas.rarytas_restaurantside.entity.Order;

import java.util.List;

public interface OrderRepository extends CustomRepository<Order, Integer> {
    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE is_paid = false AND take_away = false AND is_resolved = false", nativeQuery = true)
    List<Order> findAllNotPaid();

    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE take_away = true AND is_resolved = false", nativeQuery = true)
    List<Order> findAllTakeAway();

    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE is_resolved = true LIMIT 50", nativeQuery = true)
    List<Order> findAllResolvedLimit50();

}