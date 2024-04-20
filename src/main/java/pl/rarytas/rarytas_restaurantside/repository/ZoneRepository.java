package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rarytas.rarytas_restaurantside.entity.Zone;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Integer> {
    List<Zone> findAllByOrderByDisplayOrder();
}