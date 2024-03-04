package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    Category findById(Integer id) throws LocalizedException;

    void save(Category category);

    void delete(Category category);
}
