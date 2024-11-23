package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import javax.naming.AuthenticationException;
import java.util.Set;

public interface MenuService {

    Set<MenuSimpleDTO> findAll(Long activeRestaurantId) throws LocalizedException, AuthenticationException;

    MenuSimpleDTO findById(Long id, Long activeRestaurantId) throws LocalizedException;

    void save(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception;

    void update(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception;

    void delete(Long id, Long activeRestaurantId) throws LocalizedException, AuthenticationException;
}
