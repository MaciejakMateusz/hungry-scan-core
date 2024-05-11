package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Settings;

public interface SettingsService {
    Settings getSettings();

    void save(Settings settings);
}