package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.Query;
import pl.rarytas.rarytas_restaurantside.entity.Order;

import java.util.List;

public interface OrderRepository extends CustomRepository<Order, Integer> {
    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE is_paid = false", nativeQuery = true)
    List<Order> findAllNotPaid();

    @Query(value = "SELECT * FROM rarytas_testing.orders WHERE is_paid = true LIMIT 50", nativeQuery = true)
    List<Order> findAllPaidLimit50();
}