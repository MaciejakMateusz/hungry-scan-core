package pl.rarytas.rarytas_restaurantside.repository.archive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;

import java.time.LocalDate;

public interface HistoryOrderRepository extends JpaRepository<HistoryOrder, Long> {

    @Query("SELECT ho FROM HistoryOrder ho WHERE ho.forTakeAway = false")
    Page<HistoryOrder> findAllDineIn(Pageable pageable);

    @Query("SELECT ho FROM HistoryOrder ho WHERE ho.forTakeAway = true")
    Page<HistoryOrder> findAllForTakeAway(Pageable pageable);

    @Query("SELECT ho FROM HistoryOrder ho WHERE ho.orderDate BETWEEN :dateFrom AND :dateTo AND ho.forTakeAway = false")
    Page<HistoryOrder> findDineInByDates(Pageable pageable,
                                         @Param("dateFrom") LocalDate dateFrom,
                                         @Param("dateTo") LocalDate dateTo);

    @Query("SELECT ho FROM HistoryOrder ho WHERE ho.orderDate BETWEEN :dateFrom AND :dateTo AND ho.forTakeAway = true")
    Page<HistoryOrder> findTakeAwayByDates(Pageable pageable,
                                           @Param("dateFrom") LocalDate dateFrom,
                                           @Param("dateTo") LocalDate dateTo);
}