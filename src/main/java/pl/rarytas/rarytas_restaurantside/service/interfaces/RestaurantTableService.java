package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableService {
    List<RestaurantTable> findAll();

    Optional<RestaurantTable> findById(Integer id);
}
