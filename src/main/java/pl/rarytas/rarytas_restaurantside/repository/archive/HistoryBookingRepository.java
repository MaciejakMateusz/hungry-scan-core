package pl.rarytas.rarytas_restaurantside.repository.archive;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryBooking;
import pl.rarytas.rarytas_restaurantside.repository.CustomRepository;

import java.time.LocalDate;
import java.util.Set;

public interface HistoryBookingRepository extends CustomRepository<HistoryBooking, Long> {
    @Query("SELECT b FROM Booking b WHERE YEAR(b.date) = :year AND WEEK(b.date) = :week")
    Set<HistoryBooking> findAllByWeek(@Param("year") int year, @Param("week") int week);

    Set<HistoryBooking> findAllByDate(LocalDate date);
}