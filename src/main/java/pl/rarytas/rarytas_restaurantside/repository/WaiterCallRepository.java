package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;

public interface WaiterCallRepository extends JpaRepository<WaiterCall, Integer> {
}
