package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableServiceInterface;

import java.util.List;
import java.util.Optional;


@Service
public class RestaurantTableService implements RestaurantTableServiceInterface {

    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public List<RestaurantTable> findAll() {
        return restaurantTableRepository.findAll();
    }

    @Override
    public Optional<RestaurantTable> findById(Integer id) {
        return restaurantTableRepository.findById(id);
    }
}
