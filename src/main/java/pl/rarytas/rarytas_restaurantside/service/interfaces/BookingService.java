package pl.rarytas.rarytas_restaurantside.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.time.LocalDate;

public interface BookingService {

    Booking findById(Long id) throws LocalizedException;

    void save(Booking booking) throws LocalizedException;

    void delete(Booking booking);

    Page<Booking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    Long countAll();

    Long countByDateBetween(LocalDate dateFrom, LocalDate dateTo);
}
