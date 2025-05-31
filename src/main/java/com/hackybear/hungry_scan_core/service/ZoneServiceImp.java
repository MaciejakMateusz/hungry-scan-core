package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Zone;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.ZoneRepository;
import com.hackybear.hungry_scan_core.service.interfaces.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneServiceImp implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final ExceptionHelper exceptionHelper;

    @Override
    public void save(Zone zone) {
        zoneRepository.save(zone);
    }

    @Override
    public List<Zone> findAll() {
        return zoneRepository.findAllByOrderByDisplayOrder();
    }

    @Override
    public Zone findById(Long id) throws LocalizedException {
        return zoneRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.zoneService.zoneNotFound", id));
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        Zone existingZone = findById(id);
        zoneRepository.delete(existingZone);
    }
}