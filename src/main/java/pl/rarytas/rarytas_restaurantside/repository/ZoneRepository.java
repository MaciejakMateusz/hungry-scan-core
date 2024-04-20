package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.Zone;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Integer> {
    List<Zone> findAllByOrderByDisplayOrder();

    @Query("SELECT z FROM Zone z WHERE :table IN elements(z.restaurantTables)")
    Optional<Zone> findByRestaurantTable(@Param("table") RestaurantTable table);
}