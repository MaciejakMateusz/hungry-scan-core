package com.hackybear.hungry_scan_core.cron;

import com.hackybear.hungry_scan_core.entity.Booking;
import com.hackybear.hungry_scan_core.entity.history.HistoryBooking;
import com.hackybear.hungry_scan_core.repository.BookingRepository;
import com.hackybear.hungry_scan_core.repository.history.HistoryBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class BookingArchiver {

    private final BookingRepository bookingRepository;
    private final HistoryBookingRepository historyBookingRepository;

    @Transactional
    public void checkAndArchive() {
        Set<Booking> expiredBookings =
                bookingRepository.findExpiredBookings(LocalDate.now(), LocalTime.now().minusHours(3));
        for (Booking expiredBooking : expiredBookings) {
            HistoryBooking historyBooking = new HistoryBooking(expiredBooking);
            historyBookingRepository.saveAndFlush(historyBooking);
            bookingRepository.delete(expiredBooking);
        }
    }
}