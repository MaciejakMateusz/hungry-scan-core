package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Settings;
import pl.rarytas.rarytas_restaurantside.repository.SettingsRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.SettingsService;

@Service
public class SettingsServiceImp implements SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsServiceImp(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public Settings getSettings() {
        return settingsRepository.findById(1).orElseThrow();
    }

    @Override
    public void save(Settings settings) {
        settingsRepository.save(settings);
    }
}