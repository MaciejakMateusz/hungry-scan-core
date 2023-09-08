package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderServiceInterface {
    List<Order> findAllNotPaid();

    List<Order> findAllTakeAway();

    List<Order> findAllByResolvedIsTrue();

    List<Order> findAllFinalized(boolean forTakeAway, Integer limit, Integer offset);

    Optional<Order> findFinalizedById(Integer id, boolean forTakeAway);

    List<Order> findFinalizedByDate(String date, boolean forTakeAway);

    List<Order> findAllResolvedTakeAwayLimit50();

    Optional<Order> findByTableNumber(Integer number);

    Optional<Order> findById(Integer id);

    void save(Order order);

    void saveTakeAway(Order order);

    void patch(Order order);

    void patchTakeAway(Order order);

    void finish(Integer id, boolean paid, boolean isResolved);

    void finishTakeAway(Integer id, boolean paid, boolean isResolved);

    void callWaiter(Order order);

    void resolveWaiterCall(Integer id, boolean waiterCalled);

    boolean existsByIdAndForTakeAwayAndResolved(Integer id, boolean forTakeAway);
}
