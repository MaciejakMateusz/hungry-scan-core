package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface MenuItemService {
    void save(MenuItem menuItem);

    List<MenuItem> findAll();

    MenuItem findById(Integer id) throws LocalizedException;

    void changeCategory(Integer itemId, Integer categoryId) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}
