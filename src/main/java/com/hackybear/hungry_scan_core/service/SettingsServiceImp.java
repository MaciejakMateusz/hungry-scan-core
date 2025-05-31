package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.dto.mapper.SettingsMapper;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import com.hackybear.hungry_scan_core.service.interfaces.SettingsService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsServiceImp implements SettingsService {

    private final SettingsRepository settingsRepository;
    private final SettingsMapper settingsMapper;
    private final UserService userService;

    @Override
    public SettingsDTO getSettings() throws LocalizedException {
        Long restaurantId = userService.getActiveRestaurantId();
        Settings settings = settingsRepository.findByRestaurantId(restaurantId);
        return settingsMapper.toDTO(settings);
    }

}