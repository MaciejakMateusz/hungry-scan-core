package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.Set;

public interface RestaurantService {

    Set<Restaurant> findAll(User currentUser);

    Restaurant findById(Long id) throws LocalizedException;

    void save(Restaurant restaurant, User currentUser);

    void delete(Long id, User currentUser) throws LocalizedException;

    Restaurant findByToken(String token) throws LocalizedException;
}
