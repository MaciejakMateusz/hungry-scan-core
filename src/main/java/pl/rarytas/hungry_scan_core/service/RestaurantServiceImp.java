package pl.rarytas.hungry_scan_core.service;

import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.Restaurant;
import pl.rarytas.hungry_scan_core.exception.ExceptionHelper;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.repository.RestaurantRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.RestaurantService;

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
    public Restaurant findById(Integer id) throws LocalizedException {
        return restaurantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFound", id));
    }

    @Override
    public void save(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        Restaurant existingRestaurant = findById(id);
        restaurantRepository.delete(existingRestaurant);
    }
}