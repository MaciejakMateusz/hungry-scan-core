package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
