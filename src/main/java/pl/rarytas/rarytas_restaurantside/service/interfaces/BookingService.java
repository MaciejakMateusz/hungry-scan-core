package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    void save(Booking booking) throws LocalizedException;

    void delete(Booking booking);

    List<Booking> findAllByWeek(int year, int week);

    List<Booking> findAllByDate(LocalDate date);
}
