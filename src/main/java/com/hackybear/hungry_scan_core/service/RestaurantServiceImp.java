package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ExceptionHelper exceptionHelper;

    public RestaurantServiceImp(RestaurantRepository restaurantRepository, ExceptionHelper exceptionHelper) {
        this.restaurantRepository = restaurantRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant findById(Long id) throws LocalizedException {
        return restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound", id));
    }

    @Override
    public void save(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        Restaurant existingRestaurant = findById(id);
        restaurantRepository.delete(existingRestaurant);
    }

    @Override
    public Restaurant findByToken(String token) throws LocalizedException {
        return restaurantRepository.findByToken(token).orElseThrow(
                exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFoundByToken"));
    }
}
