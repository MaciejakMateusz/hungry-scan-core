package pl.rarytas.hungry_scan_core.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.hungry_scan_core.entity.Booking;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

import java.time.LocalDate;

public interface BookingService {

    Booking findById(Long id) throws LocalizedException;

    void save(Booking booking) throws LocalizedException;

    void delete(Long id) throws LocalizedException;

    Page<Booking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    Long countAll();

    Long countByDateBetween(LocalDate dateFrom, LocalDate dateTo);
}
