package pl.rarytas.rarytas_restaurantside.repository;

import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.util.Optional;

public interface RestaurantTableRepository extends CustomRepository<RestaurantTable, Integer> {
    Optional<RestaurantTable> findByToken(String token);
}
