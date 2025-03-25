package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface MenuItemService {

    MenuItemFormDTO findById(Long id) throws LocalizedException;

    void save(MenuItemFormDTO menuItem, Long activeMenuId) throws Exception;

    void update(MenuItemFormDTO menuItem, Long activeMenuId) throws Exception;

    List<MenuItemSimpleDTO> updateDisplayOrders(List<MenuItemSimpleDTO> menuItems, Long activeMenuId);

    List<MenuItemSimpleDTO> filterByName(String name);

    List<MenuItemSimpleDTO> delete(Long id, Long activeMenuId) throws LocalizedException;

    void persistViewEvent(Long menuItemId, Long activeMenuId) throws LocalizedException;
}
