package pl.rarytas.hungry_scan_core.service;

import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.Settings;
import pl.rarytas.hungry_scan_core.repository.SettingsRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.SettingsService;

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