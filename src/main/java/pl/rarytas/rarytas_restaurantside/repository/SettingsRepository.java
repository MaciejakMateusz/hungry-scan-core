package pl.rarytas.rarytas_restaurantside.repository;

import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.entity.Settings;

import java.util.Optional;

public interface SettingsRepository extends CustomRepository<Settings, Integer> {
    Optional<Settings> findByRestaurant(Restaurant restaurant);
}