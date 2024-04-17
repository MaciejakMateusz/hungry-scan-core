package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Ingredient;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface IngredientService {

    void save(Ingredient ingredient);

    List<Ingredient> findAll();

    Ingredient findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;

}
