package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.MenuItem;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface MenuItemService {

    List<MenuItem> findAll();

    List<MenuItem> findAllByCategoryId(Integer id);

    MenuItem findById(Integer id) throws LocalizedException;

    void save(MenuItem menuItem);

    void delete(Integer id) throws LocalizedException;
}
