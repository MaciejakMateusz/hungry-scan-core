package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.OrganizationRestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantSimpleDTO;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public interface RestaurantService {

    Set<RestaurantSimpleDTO> findAll(User currentUser);

    TreeSet<OrganizationRestaurantDTO> findAllByOrganizationId(User currentUser);

    RestaurantDTO findCurrent(User currentUser) throws LocalizedException;

    RestaurantDTO findById(Long id) throws LocalizedException;

    void save(RestaurantDTO restaurantDTO, User currentUser) throws Exception;

    ResponseEntity<?> persistInitialRestaurant(Map<String, Object> params, User currentUser) throws Exception;

    void update(RestaurantDTO restaurantDTO, User currentUser) throws LocalizedException;

    void delete(User currentUser) throws LocalizedException;

    RestaurantDTO findByToken(String token) throws LocalizedException;

    Map<DayOfWeek, TimeRange> getOperatingHours(String token) throws LocalizedException;
}
