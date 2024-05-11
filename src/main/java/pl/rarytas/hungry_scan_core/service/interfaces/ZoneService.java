package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.Zone;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface ZoneService {

    void save(Zone zone);

    List<Zone> findAll();

    Zone findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;

}
