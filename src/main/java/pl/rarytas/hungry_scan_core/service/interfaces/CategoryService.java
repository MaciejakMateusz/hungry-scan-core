package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.Category;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    List<Category> findAllAvailable();

    Category findById(Integer id) throws LocalizedException;

    void save(Category category);

    void delete(Integer id) throws LocalizedException;
}
