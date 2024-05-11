package pl.rarytas.hungry_scan_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rarytas.hungry_scan_core.entity.Zone;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Integer> {
    List<Zone> findAllByOrderByDisplayOrder();
}