package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.MenuItemVariant;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface MenuItemVariantService {

    void save(MenuItemVariant variant);

    List<MenuItemVariant> findAll();

    MenuItemVariant findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}
