package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b WHERE YEAR(b.date) = :year AND WEEK(b.date) = :week")
    List<Booking> findAllByWeek(@Param("year") int year, @Param("week") int week);

    List<Booking> findAllByDate(LocalDate date);

}
