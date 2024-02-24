package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Settings;

public interface SettingsService {
    Settings getSettings();

    void save(Settings settings);
}