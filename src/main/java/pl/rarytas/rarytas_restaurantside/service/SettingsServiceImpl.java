package pl.rarytas.rarytas_restaurantside.service;

import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.entity.Settings;
import pl.rarytas.rarytas_restaurantside.repository.SettingsRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.SettingsService;

import java.util.Optional;

public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public Optional<Settings> findByRestaurant(Restaurant restaurant) {
        return settingsRepository.findByRestaurant(restaurant);
    }

    @Override
    public void save(Settings settings) {
        settingsRepository.save(settings);
    }
}