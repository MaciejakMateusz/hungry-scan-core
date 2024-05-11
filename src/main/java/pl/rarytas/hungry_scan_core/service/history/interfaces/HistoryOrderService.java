package pl.rarytas.hungry_scan_core.service.history.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrder;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

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
