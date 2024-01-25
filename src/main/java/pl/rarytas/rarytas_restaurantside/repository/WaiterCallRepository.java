package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;

import java.util.List;
import java.util.Optional;

public interface WaiterCallRepository extends JpaRepository<WaiterCall, Integer> {
    @Query(value = "SELECT wc FROM WaiterCall wc WHERE wc.order = :order AND wc.isResolved = :isResolved")
    Optional<WaiterCall> findByOrderAndResolved(@Param("order") Order order, @Param("isResolved") boolean isResolved);
    List<WaiterCall> findAllByOrder(Order order);
}
