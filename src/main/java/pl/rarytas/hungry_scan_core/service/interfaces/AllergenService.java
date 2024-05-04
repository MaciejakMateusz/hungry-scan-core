package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.Allergen;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface AllergenService {
    void save(Allergen allergen);

    List<Allergen> findAll();

    Allergen findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}
