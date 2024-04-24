package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface MenuItemService {

    List<MenuItem> findAll();

    List<MenuItem> findAllByCategoryId(Integer id);

    MenuItem findById(Integer id) throws LocalizedException;

    void save(MenuItem menuItem);

    void delete(Integer id) throws LocalizedException;
}
