package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.util.List;

@Slf4j
@Service
public class RestaurantTableServiceImp implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final ExceptionHelper exceptionHelper;

    public RestaurantTableServiceImp(RestaurantTableRepository restaurantTableRepository,
                                     ExceptionHelper exceptionHelper) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.exceptionHelper = exceptionHelper;
    }


    @Override
    public List<RestaurantTable> findAll() {
        return restaurantTableRepository.findAll();
    }

    @Override
    public RestaurantTable findById(Integer id) throws LocalizedException {
        return restaurantTableRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantTableService.tableNotFound", id));
    }

    @Override
    public RestaurantTable findByToken(String token) throws LocalizedException {
        return restaurantTableRepository.findByToken(token)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.general.accessDenied"));
    }

    @Override
    public void save(RestaurantTable restaurantTable) {
        restaurantTableRepository.save(restaurantTable);
    }

    @Override
    public void toggleActivation(Integer id) throws LocalizedException {
        RestaurantTable table = findById(id);
        table.setActive(!table.isActive());
        save(table);
    }
}