package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Allergen;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface AllergenService {
    void save(Allergen allergen);

    List<Allergen> findAll();

    Allergen findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}
