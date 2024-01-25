package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import java.util.List;
import java.util.Optional;

public interface WaiterCallService {
    void save(WaiterCall waiterCall);
    Optional<WaiterCall> findByOrderAndResolved(Order order, boolean isResolved);
    List<WaiterCall> findAllByOrder(Order order);
    void delete(WaiterCall waiterCall);
}
