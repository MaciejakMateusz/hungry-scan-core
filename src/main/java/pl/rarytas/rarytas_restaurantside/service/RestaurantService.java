package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService implements RestaurantServiceInterface {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    @Override
    public Optional<Restaurant> findById(Integer id) {
        return restaurantRepository.findById(id);
    }
}
