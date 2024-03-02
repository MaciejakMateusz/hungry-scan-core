package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService extends GenericService<Category> {
    List<Category> findAll();

    Optional<Category> findById(Integer id);

    void delete(Category category);
}
