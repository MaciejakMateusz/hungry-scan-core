package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.SettingsDTO;
import com.hackybear.hungry_scan_core.dto.mapper.SettingsMapper;
import com.hackybear.hungry_scan_core.entity.Settings;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.SettingsRepository;
import com.hackybear.hungry_scan_core.service.interfaces.SettingsService;
import com.hackybear.hungry_scan_core.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsServiceImp implements SettingsService {

    private final SettingsRepository settingsRepository;
    private final SettingsMapper settingsMapper;
    private final UserService userService;

    public SettingsServiceImp(SettingsRepository settingsRepository, SettingsMapper settingsMapper, UserService userService) {
        this.settingsRepository = settingsRepository;
        this.settingsMapper = settingsMapper;
        this.userService = userService;
    }

    @Override
    public SettingsDTO getSettings() throws LocalizedException {
        Long restaurantId = userService.getActiveRestaurantId();
        Settings settings = settingsRepository.findByRestaurantId(restaurantId);
        return settingsMapper.toDTO(settings);
    }

    @Override
    @Transactional
    public void save(SettingsDTO settingsDTO) throws LocalizedException {
        Long restaurantId = userService.getActiveRestaurantId();
        Settings settings = settingsRepository.findByRestaurantId(restaurantId);
        updateSettings(settings, settingsDTO);
        settingsRepository.save(settings);
    }

    private void updateSettings(Settings settings, SettingsDTO settingsDTO) {
        settings.setOpeningTime(settingsDTO.openingTime());
        settings.setClosingTime(settingsDTO.closingTime());
        settings.setBookingDuration(settingsDTO.bookingDuration());
        settings.setLanguage(settingsDTO.language());
        settings.setEmployeeSessionTime(settingsDTO.employeeSessionTime());
        settings.setCustomerSessionTime(settingsDTO.customerSessionTime());
        settings.setCapacity(settingsDTO.capacity());
        settings.setOrderCommentAllowed(settingsDTO.orderCommentAllowed());
        settings.setWaiterCommentAllowed(settingsDTO.waiterCommentAllowed());
    }
}