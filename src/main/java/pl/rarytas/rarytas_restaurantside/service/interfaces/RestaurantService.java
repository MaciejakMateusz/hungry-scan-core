package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface RestaurantService {
    List<Restaurant> findAll();

    Restaurant findById(Integer id) throws LocalizedException;

    void save(Restaurant restaurant);

    void delete(Restaurant restaurant);
}
