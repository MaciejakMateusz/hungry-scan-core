package pl.rarytas.rarytas_restaurantside.repository.archive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;

import java.util.List;

public interface HistoryOrderRepository extends JpaRepository<HistoryOrder, Long> {

    @Query(value = "SELECT ho FROM HistoryOrder ho WHERE ho.forTakeAway = false")
    Page<HistoryOrder> findAllDineIn(Pageable pageable);

    @Query(value = "SELECT ho FROM HistoryOrder ho WHERE ho.forTakeAway = true")
    Page<HistoryOrder> findAllForTakeAway(Pageable pageable);

    @Query(value = "SELECT ho FROM HistoryOrder ho WHERE ho.orderTime LIKE %:date AND ho.forTakeAway = :forTakeAway")
    List<HistoryOrder> findByDate(@Param("date") String date,
                                  @Param("forTakeAway") boolean forTakeAway);

}