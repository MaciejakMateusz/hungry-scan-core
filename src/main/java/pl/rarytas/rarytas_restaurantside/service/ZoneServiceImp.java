package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Zone;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.ZoneRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.ZoneService;

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