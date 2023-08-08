package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.QrScan;

public interface QrScanRepository extends JpaRepository<QrScan, Integer> {
}
