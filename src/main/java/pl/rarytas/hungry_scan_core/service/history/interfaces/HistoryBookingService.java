package pl.rarytas.hungry_scan_core.service.history.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.hungry_scan_core.entity.history.HistoryBooking;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.time.LocalDate;

public interface HistoryBookingService {

    HistoryBooking findById(Long id) throws LocalizedException;

    Page<HistoryBooking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    Long countAll();

    Long countByDateBetween(LocalDate dateFrom, LocalDate dateTo);
}
