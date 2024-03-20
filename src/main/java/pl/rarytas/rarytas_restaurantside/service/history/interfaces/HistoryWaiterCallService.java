package pl.rarytas.rarytas_restaurantside.service.history.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryWaiterCall;

import java.util.List;

public interface HistoryWaiterCallService {
    void save(HistoryWaiterCall historyWaiterCall);

    List<HistoryWaiterCall> findAllByHistoryOrder(HistoryOrder historyOrder);
}
