package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Zone;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface ZoneService {

    void save(Zone zone);

    List<Zone> findAll();

    Zone findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;

}
