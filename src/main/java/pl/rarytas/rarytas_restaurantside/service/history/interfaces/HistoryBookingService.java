package pl.rarytas.rarytas_restaurantside.service.history.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryBooking;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.time.LocalDate;

public interface HistoryBookingService {

    HistoryBooking findById(Long id) throws LocalizedException;

    Page<HistoryBooking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    Long countAll();

    Long countByDateBetween(LocalDate dateFrom, LocalDate dateTo);
}
