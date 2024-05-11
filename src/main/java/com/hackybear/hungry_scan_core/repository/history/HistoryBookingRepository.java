package com.hackybear.hungry_scan_core.repository.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryBooking;
import com.hackybear.hungry_scan_core.repository.CustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface HistoryBookingRepository extends CustomRepository<HistoryBooking, Long> {
    Long countAllByDateBetween(LocalDate dateFrom, LocalDate dateTo);

    Page<HistoryBooking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);
}