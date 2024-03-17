package pl.rarytas.rarytas_restaurantside.service.history.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.time.LocalDate;

public interface HistoryOrderService {

    Page<HistoryOrder> findAllForTakeAway(Pageable pageable);

    Page<HistoryOrder> findAllDineIn(Pageable pageable);

    HistoryOrder findById(Long id) throws LocalizedException;

    Long countAll();

    Page<HistoryOrder> findDineInByDate(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    Page<HistoryOrder> findTakeAwayByDate(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    void save(HistoryOrder historyOrder);
}
