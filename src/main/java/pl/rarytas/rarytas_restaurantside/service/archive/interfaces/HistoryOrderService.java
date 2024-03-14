package pl.rarytas.rarytas_restaurantside.service.archive.interfaces;

import org.springframework.data.domain.Pageable;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.time.LocalDate;
import java.util.List;

public interface HistoryOrderService {

    List<HistoryOrder> findAllForTakeAway(Pageable pageable);

    List<HistoryOrder> findAllDineIn(Pageable pageable);

    HistoryOrder findById(Long id) throws LocalizedException;

    Long countAll();

    List<HistoryOrder> findDineInByDate(Pageable pageable, LocalDate startDate, LocalDate endDate);

    List<HistoryOrder> findTakeAwayByDate(Pageable pageable, LocalDate startDate, LocalDate endDate);

    void save(HistoryOrder historyOrder);
}
