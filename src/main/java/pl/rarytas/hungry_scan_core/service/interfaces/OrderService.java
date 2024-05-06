package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface OrderService {
    List<Order> findAll();

    List<Order> findAllTakeAway();

    List<Order> findAllDineIn();

    Order findById(Long id) throws LocalizedException;

    void saveDineIn(Order order) throws LocalizedException;

    void saveTakeAway(Order order) throws LocalizedException;

    void finishTakeAway(Long id) throws LocalizedException;

    void delete(Order order);
}