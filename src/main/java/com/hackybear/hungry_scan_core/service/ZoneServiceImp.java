package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Zone;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.ZoneRepository;
import com.hackybear.hungry_scan_core.service.interfaces.ZoneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ZoneServiceImp implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final ExceptionHelper exceptionHelper;

    public ZoneServiceImp(ZoneRepository zoneRepository, ExceptionHelper exceptionHelper) {
        this.zoneRepository = zoneRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(Zone zone) {
        zoneRepository.save(zone);
    }

    @Override
    public List<Zone> findAll() {
        return zoneRepository.findAllByOrderByDisplayOrder();
    }

    @Override
    public Zone findById(Integer id) throws LocalizedException {
        return zoneRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.zoneService.zoneNotFound", id));
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        Zone existingZone = findById(id);
        zoneRepository.delete(existingZone);
    }
}