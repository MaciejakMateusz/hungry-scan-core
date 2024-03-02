package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public interface BookingRepository extends CustomRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b WHERE YEAR(b.date) = :year AND WEEK(b.date) = :week")
    Set<Booking> findAllByWeek(@Param("year") int year, @Param("week") int week);

    Set<Booking> findAllByDate(LocalDate date);

    @Query(value = "SELECT b FROM Booking b WHERE (b.date < :currentDate) OR (b.date = :currentDate AND b.time <= :expirationTime)")
    Set<Booking> findExpiredBookings(@Param("currentDate") LocalDate currentDate, @Param("expirationTime") LocalTime expirationTime);

}