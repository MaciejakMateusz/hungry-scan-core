package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAllNotPaid();

    List<Order> findAllTakeAway();

    List<Order> findAllByResolvedIsTrue();

    Optional<Order> findFinalizedById(Integer id, boolean forTakeAway);

    Optional<Order> findByTableNumber(Integer number);

    Optional<?> findById(Integer id);

    void save(Order order) throws LocalizedException;

    void saveTakeAway(Order order);

    void requestBill(Order order) throws LocalizedException;

    void finish(Integer id, boolean paid, boolean isResolved) throws LocalizedException;

    void finishTakeAway(Integer id, boolean paid, boolean isResolved) throws LocalizedException;

    void callWaiter(Order order) throws LocalizedException;

    void resolveWaiterCall(Integer id) throws LocalizedException;

    void delete(Order order);
}
