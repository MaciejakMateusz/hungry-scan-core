package com.hackybear.hungry_scan_core.cron;

import com.hackybear.hungry_scan_core.entity.Booking;
import com.hackybear.hungry_scan_core.entity.history.HistoryBooking;
import com.hackybear.hungry_scan_core.repository.BookingRepository;
import com.hackybear.hungry_scan_core.repository.history.HistoryBookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingArchiverTest {

    private static final long EXPIRATION_TIME = 3L;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HistoryBookingRepository historyBookingRepository;

    @Autowired
    private BookingArchiver bookingArchiver;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @Order(2)
    void shouldFindArchive() {
        Set<Booking> expiredBookings = bookingRepository.findExpiredBookings(LocalDate.now(), LocalTime.now().minusHours(EXPIRATION_TIME));
        assertFalse(expiredBookings.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    @Order(3)
    void shouldCheckAndArchive() {
        Set<Booking> expiredBookings =
                bookingRepository.findExpiredBookings(LocalDate.now(), LocalTime.now().minusHours(EXPIRATION_TIME));
        assertFalse(expiredBookings.isEmpty());

        bookingArchiver.checkAndArchive();

        expiredBookings =
                bookingRepository.findExpiredBookings(LocalDate.now(), LocalTime.now().minusHours(EXPIRATION_TIME));
        assertTrue(expiredBookings.isEmpty());

        Page<HistoryBooking> archiveBookings =
                historyBookingRepository.findAllByDateBetween(
                        PageRequest.of(0, 20),
                        LocalDate.of(2024, 2, 23),
                        LocalDate.of(2024, 2, 28));
        assertEquals("Pierwszy", archiveBookings.getContent().getFirst().getSurname());
        assertEquals("Drugi", archiveBookings.getContent().get(1).getSurname());
    }
}