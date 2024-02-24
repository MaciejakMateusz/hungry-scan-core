package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Settings;

import java.util.Optional;

public interface SettingsService {
    Optional<Settings> findByRestaurant();

    void save(Settings settings);
}
