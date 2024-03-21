package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface OrderService {
    List<Order> findAll();

    List<Order> findAllTakeAway();

    List<Order> findAllDineIn();

    Order findById(Long id) throws LocalizedException;

    Order findByTableNumber(Integer tableNumber) throws LocalizedException;

    void save(Order order) throws LocalizedException;

    void orderMoreDishes(Order order) throws LocalizedException;

    void saveTakeAway(Order order);

    void requestBill(Long id, String paymentMethod) throws LocalizedException;

    void finish(Long id) throws LocalizedException;

    void finishTakeAway(Long id) throws LocalizedException;

    void callWaiter(Long id) throws LocalizedException;

    void resolveWaiterCall(Long id) throws LocalizedException;

    void delete(Order order);
}
