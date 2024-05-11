package pl.rarytas.hungry_scan_core.service.history.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.hungry_scan_core.entity.Feedback;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrderSummary;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.time.LocalDate;

public interface HistoryOrderSummaryService {

    HistoryOrderSummary findById(Long id) throws LocalizedException;

    Long countAll();

    Page<HistoryOrderSummary> findByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    void save(HistoryOrderSummary historyOrderSummary);

    void leaveFeedback(Feedback feedback) throws LocalizedException;
}
