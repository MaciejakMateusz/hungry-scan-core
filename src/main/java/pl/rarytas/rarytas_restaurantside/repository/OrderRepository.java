package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.Order;

import java.util.List;

@Repository
public interface OrderRepository extends CustomRepository<Order, Long> {

    @Query(value = "SELECT o FROM Order o WHERE o.isForTakeAway = false")
    List<Order> findAllDineIn();

    @Query(value = "SELECT o FROM Order o WHERE o.isForTakeAway = true")
    List<Order> findAllTakeAway();

}