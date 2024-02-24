package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.entity.Settings;
import pl.rarytas.rarytas_restaurantside.repository.SettingsRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.SettingsService;

import java.util.Optional;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;
    private final RestaurantService restaurantService;
    private final Environment environment;

    public SettingsServiceImpl(SettingsRepository settingsRepository, RestaurantService restaurantService, Environment environment) {
        this.settingsRepository = settingsRepository;
        this.restaurantService = restaurantService;
        this.environment = environment;
    }

    @Override
    public Optional<Settings> findByRestaurant() {
        String restaurantId = environment.getProperty("RESTAURANT_ID");
        assert restaurantId != null;
        Restaurant restaurant = restaurantService.findById(Integer.valueOf(restaurantId)).orElseThrow();
        return settingsRepository.findByRestaurant(restaurant);
    }

    @Override
    public void save(Settings settings) {
        settingsRepository.save(settings);
    }
}