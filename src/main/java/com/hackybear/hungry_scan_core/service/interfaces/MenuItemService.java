package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuItemFormDTO;
import com.hackybear.hungry_scan_core.dto.MenuItemSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static com.hackybear.hungry_scan_core.utility.Fields.CATEGORIES_ALL;
import static com.hackybear.hungry_scan_core.utility.Fields.CATEGORIES_AVAILABLE;

public interface MenuItemService {

    MenuItemFormDTO findById(Long id) throws LocalizedException;

    void save(MenuItemFormDTO menuItem, Long activeMenuId, MultipartFile image) throws Exception;

    void update(MenuItemFormDTO menuItem, Long activeMenuId, MultipartFile image) throws Exception;

    void updateDisplayOrders(List<MenuItemSimpleDTO> menuItems, Long activeMenuId);

    Set<MenuItemFormDTO> filterByName(String name);

    void delete(Long id, Long activeMenuId) throws LocalizedException;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#menuId"),
            @CacheEvict(value = CATEGORIES_AVAILABLE, key = "#menuId"),
    })
    ResponseEntity<?> switchCategory(Long menuId, Long menuItemId, Long newCategoryId);

    void persistViewEvent(Long menuItemId, Long activeMenuId) throws LocalizedException;

}
