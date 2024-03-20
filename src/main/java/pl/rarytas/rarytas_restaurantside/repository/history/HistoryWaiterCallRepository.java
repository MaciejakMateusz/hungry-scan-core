package pl.rarytas.rarytas_restaurantside.repository.history;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryWaiterCall;

import java.util.List;

public interface HistoryWaiterCallRepository extends JpaRepository<HistoryWaiterCall, Long> {
    List<HistoryWaiterCall> findAllByHistoryOrder(HistoryOrder historyOrder);
}