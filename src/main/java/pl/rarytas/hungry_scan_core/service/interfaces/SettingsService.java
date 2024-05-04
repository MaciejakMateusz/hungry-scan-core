package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.Settings;

public interface SettingsService {
    Settings getSettings();

    void save(Settings settings);
}