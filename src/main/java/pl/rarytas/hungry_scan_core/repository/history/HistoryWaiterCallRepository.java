package pl.rarytas.hungry_scan_core.repository.history;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrder;
import pl.rarytas.hungry_scan_core.entity.history.HistoryWaiterCall;

import java.util.List;

public interface HistoryWaiterCallRepository extends JpaRepository<HistoryWaiterCall, Long> {
    List<HistoryWaiterCall> findAllByHistoryOrder(HistoryOrder historyOrder);
}