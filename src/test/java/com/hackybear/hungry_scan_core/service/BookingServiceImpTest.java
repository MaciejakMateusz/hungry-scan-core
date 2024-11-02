package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Booking;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.BookingService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceImpTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "matimemek@test.com")
    void shouldBookTable3() throws LocalizedException {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(12, 0),
                (short) 2,
                "Maciejak",
                3L);
        bookingService.save(booking);
        Page<Booking> foundBookings =
                bookingService.findAllByDateBetween(PageRequest.of(0, 20), bookingDate, bookingDate);
        assertFalse(foundBookings.isEmpty());
        assertEquals("Maciejak", foundBookings.getContent().stream().findFirst().orElseThrow().getSurname());
        assertEquals((short) 2, foundBookings.getContent().stream().findFirst().orElseThrow().getNumOfPpl());
    }

    @Test
    void shouldNotBookTable3() {
        Booking booking = createBooking(
                LocalDate.of(2024, 2, 23),
                LocalTime.of(19, 30),
                (short) 3,
                "Górecki",
                3L);
        assertThrows(LocalizedException.class, () -> bookingService.save(booking));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "matimemek@test.com")
    void shouldBookTable2() throws LocalizedException {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(13, 0),
                (short) 4,
                "Makaron",
                2L);
        bookingService.save(booking);
        Page<Booking> foundBookings =
                bookingService.findAllByDateBetween(PageRequest.of(0, 20), bookingDate, bookingDate);
        assertFalse(foundBookings.isEmpty());
        assertEquals("Makaron", foundBookings.getContent().stream().findFirst().orElseThrow().getSurname());
    }

    @Test
    @WithMockUser(username = "matimemek@test.com")
    void shouldNotBookTableBeforeOpening() {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(6, 0),
                (short) 2,
                "Poranny",
                7L);
        assertThrows(LocalizedException.class, () -> bookingService.save(booking));
    }

    @Test
    @WithMockUser(username = "matimemek@test.com")
    void shouldNotBookTableAfterClosing() {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking = createBooking(
                bookingDate,
                LocalTime.of(23, 0),
                (short) 2,
                "Nocny",
                9L);
        assertThrows(LocalizedException.class, () -> bookingService.save(booking));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(username = "matimemek@test.com")
    void shouldNotLetBookATableTwice() throws LocalizedException {
        LocalDate bookingDate = LocalDate.now().plusDays(2L);
        Booking booking1 = createBooking(
                bookingDate,
                LocalTime.of(15, 0),
                (short) 4,
                "Pierwszy",
                12L);
        Booking booking2 = createBooking(
                bookingDate,
                LocalTime.of(14, 30),
                (short) 2,
                "Drugi",
                12L);
        bookingService.save(booking1);
        assertThrows(LocalizedException.class, () -> bookingService.save(booking2));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback
    public void shouldRemoveBooking() throws LocalizedException {
        Booking foundBooking = bookingService.findById(2L);
        assertNotNull(foundBooking);

        bookingService.delete(2L);

        assertThrows(LocalizedException.class, () -> bookingService.findById(2L));
    }

    private Booking createBooking(LocalDate date,
                                  LocalTime time,
                                  Short numOfPpl,
                                  String surname,
                                  Long tableId) {
        Booking booking = new Booking();
        booking.setDate(date);
        booking.setTime(time);
        booking.setNumOfPpl(numOfPpl);
        booking.setSurname(surname);
        RestaurantTable restaurantTable;
        try {
            restaurantTable = restaurantTableService.findById(tableId);
        } catch (LocalizedException e) {
            throw new RuntimeException(e);
        }

        booking.setRestaurantTables(Collections.singleton(restaurantTable));
        return booking;
    }
}