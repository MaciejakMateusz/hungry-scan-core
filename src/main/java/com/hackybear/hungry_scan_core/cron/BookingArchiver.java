package com.hackybear.hungry_scan_core.cron;

import com.hackybear.hungry_scan_core.entity.Booking;
import com.hackybear.hungry_scan_core.entity.history.HistoryBooking;
import com.hackybear.hungry_scan_core.repository.BookingRepository;
import com.hackybear.hungry_scan_core.repository.history.HistoryBookingRepository;
import com.hackybear.hungry_scan_core.service.interfaces.SettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Slf4j
@Component
public class BookingArchiver {

    private final BookingRepository bookingRepository;
    private final HistoryBookingRepository historyBookingRepository;
    private final SettingsService settingsService;

    public BookingArchiver(BookingRepository bookingRepository,
                           HistoryBookingRepository historyBookingRepository,
                           SettingsService settingsService) {
        this.bookingRepository = bookingRepository;
        this.historyBookingRepository = historyBookingRepository;
        this.settingsService = settingsService;
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 60000 * 5)
    @Transactional
    public void checkAndArchive() {
        log.info("Running checkAndArchive() job...");
        long expirationTime = settingsService.getSettings().getCustomerSessionTime();
        Set<Booking> expiredBookings =
                bookingRepository.findExpiredBookings(LocalDate.now(), LocalTime.now().minusHours(expirationTime));
        for (Booking expiredBooking : expiredBookings) {
            HistoryBooking historyBooking = new HistoryBooking(expiredBooking);
            historyBookingRepository.saveAndFlush(historyBooking);
            bookingRepository.delete(expiredBooking);
        }
        log.info("Finished checkAndArchive() job.");
    }
}