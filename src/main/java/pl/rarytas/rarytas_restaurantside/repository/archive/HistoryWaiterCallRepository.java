package pl.rarytas.rarytas_restaurantside.repository.archive;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryWaiterCall;

public interface HistoryWaiterCallRepository extends JpaRepository<HistoryWaiterCall, Long> {
}