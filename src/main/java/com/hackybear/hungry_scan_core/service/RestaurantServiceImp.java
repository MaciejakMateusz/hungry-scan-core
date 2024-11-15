package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Service
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ExceptionHelper exceptionHelper;

    public RestaurantServiceImp(RestaurantRepository restaurantRepository,
                                ExceptionHelper exceptionHelper) {
        this.restaurantRepository = restaurantRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public Set<Restaurant> findAll(User currentUser) {
        return currentUser.getRestaurants();
    }

    @Override
    @Cacheable(value = RESTAURANT_ID, key = "#id")
    public Restaurant findById(Long id) throws LocalizedException {
        return getById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#restaurant.getId()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    })
    public void save(Restaurant restaurant, User currentUser) {
        restaurantRepository.save(restaurant);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#id"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    })
    public void delete(Long id, User currentUser) throws LocalizedException {
        Restaurant existingRestaurant = getById(id);
        restaurantRepository.delete(existingRestaurant);
    }

    @Override
    @Cacheable(value = RESTAURANT_TOKEN, key = "#token")
    public Restaurant findByToken(String token) throws LocalizedException {
        return restaurantRepository.findByToken(token).orElseThrow(
                exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFoundByToken"));
    }

    private Restaurant getById(Long id) throws LocalizedException {
        return restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound", id));
    }
}
