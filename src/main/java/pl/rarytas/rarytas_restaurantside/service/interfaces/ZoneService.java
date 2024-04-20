package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Zone;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface ZoneService {

    void save(Zone zone);

    List<Zone> findAll();

    Zone findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;

}
