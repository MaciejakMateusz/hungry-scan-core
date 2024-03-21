package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;

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
