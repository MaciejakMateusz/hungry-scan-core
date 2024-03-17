package pl.rarytas.rarytas_restaurantside.repository.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryBooking;
import pl.rarytas.rarytas_restaurantside.repository.CustomRepository;

import java.time.LocalDate;

public interface HistoryBookingRepository extends CustomRepository<HistoryBooking, Long> {
    Long countAllByDateBetween(LocalDate dateFrom, LocalDate dateTo);

    Page<HistoryBooking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);
}