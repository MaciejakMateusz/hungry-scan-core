package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
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
import java.util.stream.Collectors;

import static com.hackybear.hungry_scan_core.utility.Fields.*;

@Service
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ExceptionHelper exceptionHelper;
    private final RestaurantMapper restaurantMapper;

    public RestaurantServiceImp(RestaurantRepository restaurantRepository,
                                ExceptionHelper exceptionHelper,
                                RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.exceptionHelper = exceptionHelper;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    @Cacheable(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    public Set<RestaurantDTO> findAll(User currentUser) {
        Set<Restaurant> restaurants = currentUser.getRestaurants();
        return restaurants.stream().map(restaurantMapper::toDTO).collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = RESTAURANT_ID, key = "#id")
    public RestaurantDTO findById(Long id) throws LocalizedException {
        return getDTOById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#restaurantDTO.id()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    })
    public void save(RestaurantDTO restaurantDTO, User currentUser) {
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO);
        restaurantRepository.save(restaurant);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#restaurantDTO.id()"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    })
    public void update(RestaurantDTO restaurantDTO, User currentUser) throws LocalizedException {
        Restaurant restaurant = getById(restaurantDTO.id());
        restaurant.setName(restaurantDTO.name());
        restaurant.setAddress(restaurantDTO.address());
        restaurantRepository.save(restaurant);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = RESTAURANT_ID, key = "#id"),
            @CacheEvict(value = RESTAURANTS_ALL, key = "#currentUser.getId()")
    })
    public void delete(Long id, User currentUser) throws LocalizedException {
        restaurantRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = RESTAURANT_TOKEN, key = "#token")
    public RestaurantDTO findByToken(String token) throws LocalizedException {
        Restaurant restaurant = restaurantRepository.findByToken(token)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFoundByToken"));
        return restaurantMapper.toDTO(restaurant);
    }

    private RestaurantDTO getDTOById(Long id) throws LocalizedException {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound", id));
        return restaurantMapper.toDTO(restaurant);
    }

    private Restaurant getById(Long id) throws LocalizedException {
        return restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound", id));
    }
}
