package pl.rarytas.rarytas_restaurantside.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface MenuItemService {
    void save(MenuItem menuItem, MultipartFile file);

    List<MenuItem> findAll();

    MenuItem findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}
