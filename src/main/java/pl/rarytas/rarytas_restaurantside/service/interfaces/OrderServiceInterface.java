package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderServiceInterface {
    List<Order> findAllNotPaid();

    Optional<Order> findById(Integer id);

    void saveOrPatch(Order order);
}
