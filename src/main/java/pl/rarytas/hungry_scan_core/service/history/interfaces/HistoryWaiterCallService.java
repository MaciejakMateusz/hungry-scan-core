package pl.rarytas.hungry_scan_core.service.history.interfaces;

import pl.rarytas.hungry_scan_core.entity.history.HistoryOrder;
import pl.rarytas.hungry_scan_core.entity.history.HistoryWaiterCall;

import java.util.List;

public interface HistoryWaiterCallService {
    void save(HistoryWaiterCall historyWaiterCall);

    List<HistoryWaiterCall> findAllByHistoryOrder(HistoryOrder historyOrder);
}
