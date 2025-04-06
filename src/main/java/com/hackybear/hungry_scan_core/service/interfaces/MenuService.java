package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Set;

public interface MenuService {

    Set<MenuSimpleDTO> findAll(Long activeRestaurantId) throws LocalizedException, AuthenticationException;

    MenuSimpleDTO findById(Long id, Long activeRestaurantId) throws LocalizedException;

    void save(MenuSimpleDTO menuDTO, User currentUser) throws Exception;

    void update(MenuSimpleDTO menuDTO, Long activeRestaurantId) throws Exception;

    void updatePlans(List<MenuSimpleDTO> menuDTOs, Long activeRestaurantId) throws Exception;

    void switchStandard(Long newId, Long activeRestaurantId);

    void delete(Long id, Long activeRestaurantId) throws LocalizedException, AuthenticationException;
}
