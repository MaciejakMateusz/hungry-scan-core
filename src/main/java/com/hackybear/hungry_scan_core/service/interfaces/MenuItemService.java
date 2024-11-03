package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface MenuItemService {

    MenuItemFormDTO findById(Long id) throws LocalizedException;

    void save(MenuItemFormDTO menuItem) throws Exception;

    void update(MenuItemFormDTO menuItem) throws Exception;

    List<MenuItemSimpleDTO> updateDisplayOrders(List<MenuItemSimpleDTO> menuItems);

    List<MenuItemSimpleDTO> filterByName(String name);

    List<MenuItemSimpleDTO> delete(Long id) throws LocalizedException;
}
