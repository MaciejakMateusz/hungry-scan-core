package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

public interface RestaurantService {

    Set<RestaurantDTO> findAll(User currentUser);

    RestaurantDTO findById(Long id) throws LocalizedException;

    Restaurant save(RestaurantDTO restaurantDTO, User currentUser);

    ResponseEntity<?> getCreateFirstResponse(Map<String, Object> params, User currentUser);

    void update(RestaurantDTO restaurantDTO, User currentUser) throws LocalizedException;

    void delete(Long id, User currentUser) throws LocalizedException;

    RestaurantDTO findByToken(String token) throws LocalizedException;
}
