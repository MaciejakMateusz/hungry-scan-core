package pl.rarytas.rarytas_restaurantside.repository.history;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryWaiterCall;

public interface HistoryWaiterCallRepository extends JpaRepository<HistoryWaiterCall, Long> {
}