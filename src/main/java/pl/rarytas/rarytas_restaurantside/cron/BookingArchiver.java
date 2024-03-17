package pl.rarytas.rarytas_restaurantside.cron;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryBooking;
import pl.rarytas.rarytas_restaurantside.repository.BookingRepository;
import pl.rarytas.rarytas_restaurantside.repository.history.HistoryBookingRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Component
public class BookingArchiver {

    private static final long EXPIRATION_TIME = 3L;

    private final BookingRepository bookingRepository;
    private final HistoryBookingRepository historyBookingRepository;
    private final RestaurantTableService restaurantTableService;

    public BookingArchiver(BookingRepository bookingRepository, HistoryBookingRepository historyBookingRepository, RestaurantTableService restaurantTableService) {
        this.bookingRepository = bookingRepository;
        this.historyBookingRepository = historyBookingRepository;
        this.restaurantTableService = restaurantTableService;
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 60000 * 5)
    @Transactional
    public void checkAndArchive() {
        Set<Booking> expiredBookings =
                bookingRepository.findExpiredBookings(LocalDate.now(), LocalTime.now().minusHours(EXPIRATION_TIME));
        for (Booking expiredBooking : expiredBookings) {
            historyBookingRepository.saveAndFlush(new HistoryBooking(expiredBooking));
            restaurantTableService.removeBooking(expiredBooking);
            bookingRepository.delete(expiredBooking);
        }
    }
}