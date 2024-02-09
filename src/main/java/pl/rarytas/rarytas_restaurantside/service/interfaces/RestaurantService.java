package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    List<Restaurant> findAll();

    Optional<Restaurant> findById(Integer id);

    void save(Restaurant restaurant);

    void delete(Restaurant restaurant);
}
