package pl.rarytas.rarytas_restaurantside.service.archive.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryBooking;

import java.time.LocalDate;
import java.util.Set;

public interface HistoryBookingService {

    void save(HistoryBooking historyBooking);

    Set<HistoryBooking> findAllByWeek(int year, int week);

    Set<HistoryBooking> findAllByDate(LocalDate date);
}
