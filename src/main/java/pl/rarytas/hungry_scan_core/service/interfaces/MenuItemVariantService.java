package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.MenuItemVariant;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface MenuItemVariantService {

    void save(MenuItemVariant variant);

    List<MenuItemVariant> findAll();

    MenuItemVariant findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}
