package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface RestaurantService {
    List<Restaurant> findAll();

    Restaurant findById(Integer id) throws LocalizedException;

    void save(Restaurant restaurant);

    void delete(Integer id) throws LocalizedException;
}