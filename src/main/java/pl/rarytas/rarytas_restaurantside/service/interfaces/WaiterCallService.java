package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;

import java.util.Optional;

public interface WaiterCallService {
    void callWaiter(WaiterCall waiterCall);
    Optional<WaiterCall> findByOrder(Order order);
    void delete(WaiterCall waiterCall);
}
