package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends CustomRepository<Booking, Long> {

    Page<Booking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    @Query("SELECT b FROM Booking b WHERE (b.date < :currentDate) OR (b.date = :currentDate AND b.time <= :expirationTime)")
    Set<Booking> findExpiredBookings(@Param("currentDate") LocalDate currentDate,
                                     @Param("expirationTime") LocalTime expirationTime);

    Long countAllByDateBetween(LocalDate dateFrom, LocalDate dateTo);

    List<Booking> findAllByRestaurantTablesId(Long tableId);
}