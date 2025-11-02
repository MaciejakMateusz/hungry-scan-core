package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface MenuItemService {

    MenuItemFormDTO findById(Long id) throws LocalizedException;

    ResponseEntity<?> save(MenuItemFormDTO menuItem, Long activeMenuId, MultipartFile image) throws Exception;

    ResponseEntity<?> update(MenuItemFormDTO menuItem, Long activeMenuId, MultipartFile image) throws Exception;

    void updateDisplayOrders(List<MenuItemSimpleDTO> menuItems, Long activeMenuId);

    Set<MenuItemFormDTO> filterByName(String name);

    void delete(Long id, Long activeMenuId) throws LocalizedException;

    ResponseEntity<?> switchCategory(Long menuId, Long menuItemId, Long newCategoryId);

    void persistViewEvent(Long menuItemId, Long activeMenuId) throws LocalizedException;

}
