package pl.rarytas.hungry_scan_core.repository;

import org.springframework.stereotype.Repository;
import pl.rarytas.hungry_scan_core.entity.RestaurantTable;

import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends CustomRepository<RestaurantTable, Integer> {

    Optional<RestaurantTable> findByToken(String token);

    Optional<RestaurantTable> findByNumber(Integer number);

    boolean existsByNumber(Integer number);
}
