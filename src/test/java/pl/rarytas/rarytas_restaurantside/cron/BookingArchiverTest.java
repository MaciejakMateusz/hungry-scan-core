package pl.rarytas.rarytas_restaurantside.cron;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryBooking;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.BookingRepository;
import pl.rarytas.rarytas_restaurantside.repository.history.HistoryBookingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingArchiverTest {

    private static final long EXPIRATION_TIME = 3L;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HistoryBookingRepository historyBookingRepository;

    @Autowired
    private BookingArchiver bookingArchiver;

    @Test
    void shouldFindArchive() {
        Set<Booking> expiredBookings = bookingRepository.findExpiredBookings(LocalDate.now(), LocalTime.now().minusHours(EXPIRATION_TIME));
        assertFalse(expiredBookings.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void shouldCheckAndArchive() throws LocalizedException {
        bookingArchiver.checkAndArchive();
        Set<Booking> expiredBookings = bookingRepository.findExpiredBookings(LocalDate.now(), LocalTime.now().minusHours(EXPIRATION_TIME));
        assertTrue(expiredBookings.isEmpty());
        LocalDate testDate = LocalDate.of(2024, 2, 23);
        Page<HistoryBooking> archiveBookings =
                historyBookingRepository.findAllByDateBetween(PageRequest.of(0, 20), testDate, testDate);
        assertFalse(archiveBookings.isEmpty());
    }
}