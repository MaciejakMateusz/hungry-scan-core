package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends CustomRepository<RestaurantTable, Integer> {

    Optional<RestaurantTable> findByToken(String token);

    Optional<RestaurantTable> findByNumber(Integer number);

    boolean existsByNumber(Integer number);
}
