package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.entity.WaiterCall;

import java.util.List;
import java.util.Optional;

public interface WaiterCallService {
    void save(WaiterCall waiterCall);

    Optional<WaiterCall> findByOrderAndResolved(Order order, boolean isResolved);

    List<WaiterCall> findAllByOrder(Order order);

    void delete(WaiterCall waiterCall);
}