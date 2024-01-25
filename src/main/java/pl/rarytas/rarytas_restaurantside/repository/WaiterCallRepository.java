package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;

import java.util.Optional;

public interface WaiterCallRepository extends JpaRepository<WaiterCall, Integer> {
    Optional<WaiterCall> findByOrder(Order order);
}
