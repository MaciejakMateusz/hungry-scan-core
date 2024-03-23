package pl.rarytas.rarytas_restaurantside.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rarytas.rarytas_restaurantside.entity.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends CustomRepository<Booking, Long> {

    Page<Booking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    @Query("SELECT b FROM Booking b WHERE (b.date < :currentDate) OR (b.date = :currentDate AND b.time <= :expirationTime)")
    Set<Booking> findExpiredBookings(@Param("currentDate") LocalDate currentDate,
                                     @Param("expirationTime") LocalTime expirationTime);

    Long countAllByDateBetween(LocalDate dateFrom, LocalDate dateTo);

    List<Booking> findAllByRestaurantTablesId(Integer tableId);
}