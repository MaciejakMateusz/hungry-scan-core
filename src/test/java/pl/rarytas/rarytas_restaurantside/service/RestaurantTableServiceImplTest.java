package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestaurantTableServiceImplTest {


    @Autowired
    private BookingService bookingService;

    @Autowired
    private RestaurantTableService restaurantTableService;


    /**
     * Testing deeper validation logic is contained within BookingServiceImplTest
     **/
    @Test
    @Order(1)
    void shouldBookTable() throws LocalizedException {
        LocalDate bookingDate = LocalDate.now().plusDays(5L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(12, 0),
                (short) 2,
                "Maciejak");
        restaurantTableService.bookTable(booking);
        Set<Booking> foundBookings = bookingService.findAllByDate(bookingDate);
        assertFalse(foundBookings.isEmpty());
    }

    @Test
    @Order(2)
    void shouldNotBookTable() {
        LocalDate bookingDate = LocalDate.now().plusDays(5L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(14, 55),
                (short) 4,
                "Górecki");
        assertThrows(LocalizedException.class, () -> restaurantTableService.bookTable(booking));
    }

    private Booking createBooking(LocalDate date,
                                  LocalTime time,
                                  Short numOfPpl,
                                  String surname) {
        Booking booking = new Booking();
        booking.setDate(date);
        booking.setTime(time);
        booking.setNumOfPpl(numOfPpl);
        booking.setSurname(surname);
        booking.setTableId(7);
        return booking;
    }
}