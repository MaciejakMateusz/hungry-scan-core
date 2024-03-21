package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.TransactionSystemException;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestaurantTableServiceImpTest {


    @Autowired
    private BookingService bookingService;

    @Autowired
    private RestaurantTableService restaurantTableService;


    @Test
    @Order(1)
    void shouldFindAll() {
        List<RestaurantTable> restaurantTables = restaurantTableService.findAll();
        assertEquals(19, restaurantTables.size());
        assertEquals("5afb9629-990a-4934-87f2-793b1aa2f35e", restaurantTables.get(3).getToken());
    }

    @Test
    @Order(2)
    void shouldFindById() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(10);
        assertEquals("88ca9c82-e630-40f2-9bf9-47f7d14f6bff", restaurantTable.getToken());
    }

    @Test
    @Order(3)
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> restaurantTableService.findById(98));
    }

    @Test
    @Order(4)
    void shouldFindByToken() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findByToken("65b6bb94-da99-4ced-8a94-5860fe95e708");
        assertEquals(15, restaurantTable.getId());
    }

    @Test
    @Order(5)
    void shouldNotFindByToken() {
        assertThrows(LocalizedException.class, () ->
                restaurantTableService.findByToken("65b6bb94-da99-yyymleko-8a94-5860fe95e708"));
    }

    @Test
    @Order(6)
    void shouldSave() throws LocalizedException {
        RestaurantTable restaurantTable = createRestaurantTable();
        restaurantTable.setId(20);
        restaurantTableService.save(restaurantTable);
        RestaurantTable persistedRestaurantTable = restaurantTableService.findById(20);
        assertNotNull(restaurantTable);
        assertEquals(restaurantTable.getToken(), persistedRestaurantTable.getToken());
    }

    @Test
    @Order(7)
    void shouldNotSave() {
        RestaurantTable restaurantTable = createRestaurantTable();
        restaurantTable.setId(21);
        restaurantTable.setToken(null);
        assertThrows(TransactionSystemException.class, () -> restaurantTableService.save(restaurantTable));
    }

    @Test
    @Order(8)
    void shouldToggleActivation() throws LocalizedException {
        RestaurantTable restaurantTable = restaurantTableService.findById(9);
        assertFalse(restaurantTable.isActive());

        restaurantTableService.toggleActivation(9);
        restaurantTable = restaurantTableService.findById(9);
        assertTrue(restaurantTable.isActive());

        restaurantTableService.toggleActivation(9);
        restaurantTable = restaurantTableService.findById(9);
        assertFalse(restaurantTable.isActive());
    }

    @Test
    @Order(9)
    void shouldNotToggle() {
        assertThrows(LocalizedException.class, () -> restaurantTableService.toggleActivation(55));
    }

    @Test
    @Order(10)
    void shouldBookTable() throws LocalizedException {
        LocalDate bookingDate = LocalDate.now().plusDays(5L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(12, 0),
                (short) 2,
                "Maciejak");
        restaurantTableService.bookTable(booking);
        Page<Booking> foundBookings =
                bookingService.findAllByDateBetween(PageRequest.of(0, 20), bookingDate, bookingDate);
        assertFalse(foundBookings.isEmpty());
    }

    /**
     * Testing deeper validation logic for bookings is contained within BookingServiceImplTest
     **/
    @Test
    @Order(11)
    void shouldNotBookTable() {
        LocalDate bookingDate = LocalDate.now().plusDays(5L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(14, 55),
                (short) 4,
                "Górecki");
        assertThrows(LocalizedException.class, () -> restaurantTableService.bookTable(booking));
    }

    @Test
    @Order(12)
    void shouldRemoveBooking() throws LocalizedException {
        Set<Booking> bookings = restaurantTableService.findById(7).getBookings();
        assertFalse(bookings.isEmpty());

        Booking booking = bookingService.findById(2L);
        restaurantTableService.removeBooking(booking);

        bookings = restaurantTableService.findById(7).getBookings();
        assertTrue(bookings.isEmpty());
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

    private RestaurantTable createRestaurantTable() {
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setToken(UUID.randomUUID().toString());
        return restaurantTable;
    }
}