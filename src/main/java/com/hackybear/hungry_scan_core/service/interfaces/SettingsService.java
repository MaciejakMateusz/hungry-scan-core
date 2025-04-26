package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

public interface SettingsService {
    SettingsDTO getSettings() throws LocalizedException;
}