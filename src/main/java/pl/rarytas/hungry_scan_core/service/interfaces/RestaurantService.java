package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.Restaurant;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface RestaurantService {
    List<Restaurant> findAll();

    Restaurant findById(Integer id) throws LocalizedException;

    void save(Restaurant restaurant);

    void delete(Integer id) throws LocalizedException;
}
