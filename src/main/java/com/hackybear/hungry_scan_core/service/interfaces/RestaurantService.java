package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantSimpleDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

public interface RestaurantService {

    Set<RestaurantSimpleDTO> findAll(User currentUser);

    RestaurantDTO findCurrent(User currentUser) throws LocalizedException;

    RestaurantDTO findById(Long id) throws LocalizedException;

    void save(RestaurantDTO restaurantDTO, User currentUser);

    ResponseEntity<?> persistInitialRestaurant(Map<String, Object> params, User currentUser);

    void update(RestaurantDTO restaurantDTO, User currentUser) throws LocalizedException;

    void delete(Long id, User currentUser) throws LocalizedException;

    RestaurantDTO findByToken(String token) throws LocalizedException;
}
