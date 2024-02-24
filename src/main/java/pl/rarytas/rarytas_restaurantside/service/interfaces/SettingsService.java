package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.entity.Settings;

import java.util.Optional;

public interface SettingsService {
    Optional<Settings> findByRestaurant(Restaurant restaurant);

    void save(Settings settings);
}
