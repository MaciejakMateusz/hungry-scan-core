package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import com.hackybear.hungry_scan_core.service.interfaces.SettingsService;
import org.springframework.stereotype.Service;

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