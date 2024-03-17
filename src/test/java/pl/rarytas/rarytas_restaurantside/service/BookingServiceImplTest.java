package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Test
    @Order(1)
    void shouldBookTable3() throws LocalizedException {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(12, 0),
                (short) 2,
                "Maciejak",
                3);
        bookingService.save(booking);
        Page<Booking> foundBookings =
                bookingService.findAllByDateBetween(PageRequest.of(0, 20), bookingDate, bookingDate);
        assertFalse(foundBookings.isEmpty());
    }

    @Test
    @Order(2)
    void shouldNotBookTable3() {
        Booking booking = createBooking(
                LocalDate.now().plusDays(2L),
                LocalTime.of(14, 0),
                (short) 3,
                "Górecki",
                3);
        assertThrows(LocalizedException.class, () -> bookingService.save(booking));
    }

    @Test
    @Order(3)
    void shouldBookTable2() throws LocalizedException {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(13, 0),
                (short) 4,
                "Makaron",
                2);
        bookingService.save(booking);
        Page<Booking> foundBookings =
                bookingService.findAllByDateBetween(PageRequest.of(0, 20), bookingDate, bookingDate);
        assertFalse(foundBookings.isEmpty());
    }

    @Test
    void shouldNotBookTableBeforeOpening() {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(6, 0),
                (short) 2,
                "Poranny",
                7);
        assertThrows(LocalizedException.class, () -> bookingService.save(booking));
    }

    @Test
    void shouldNotBookTableAfterClosing() {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(23, 0),
                (short) 2,
                "Nocny",
                9);
        assertThrows(LocalizedException.class, () -> bookingService.save(booking));
    }

    @Test
    @Transactional
    void shouldNotLetBookTwoTablesAtOnce() throws LocalizedException {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking1 = createBooking(
                bookingDate,
                LocalTime.of(15, 0),
                (short) 4,
                "Pierwszy",
                12);
        Booking booking2 = createBooking(
                bookingDate,
                LocalTime.of(14, 30),
                (short) 2,
                "Drugi",
                12);
        bookingService.save(booking1);
        assertThrows(LocalizedException.class, () -> bookingService.save(booking2));
    }

    private Booking createBooking(LocalDate date,
                                  LocalTime time,
                                  Short numOfPpl,
                                  String surname,
                                  Integer tableId) {
        Booking booking = new Booking();
        booking.setDate(date);
        booking.setTime(time);
        booking.setNumOfPpl(numOfPpl);
        booking.setSurname(surname);
        booking.setTableId(tableId);
        return booking;
    }
}