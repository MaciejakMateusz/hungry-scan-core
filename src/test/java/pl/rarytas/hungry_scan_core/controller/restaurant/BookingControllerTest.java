package pl.rarytas.hungry_scan_core.controller.restaurant;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.hungry_scan_core.entity.Booking;
import pl.rarytas.hungry_scan_core.entity.RestaurantTable;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.service.interfaces.RestaurantTableService;
import pl.rarytas.hungry_scan_core.test_utils.ApiRequestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(2)
    public void shouldGetById() throws Exception {
        Booking booking =
                apiRequestUtils.postObjectExpect200("/api/restaurant/bookings/show", 1, Booking.class);
        assertEquals("Pierwszy", booking.getSurname());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Transactional
    @Rollback
    @Order(3)
    public void shouldSaveNewBooking() throws Exception {
        Booking booking = createBooking(
                LocalDate.now().plusDays(4),
                LocalTime.of(12, 0),
                "Garnek",
                List.of(11),
                (short) 3);

        apiRequestUtils.postAndExpect200("/api/restaurant/bookings", booking);

        Booking persistedBooking =
                apiRequestUtils.postObjectExpect200("/api/restaurant/bookings/show", 3L, Booking.class);

        assertEquals(LocalDate.now().plusDays(4), persistedBooking.getDate());
        assertEquals(LocalTime.of(12, 0), persistedBooking.getTime());
        assertEquals("Garnek", persistedBooking.getSurname());
        assertEquals(1, persistedBooking.getRestaurantTables().size());
        assertEquals(11, persistedBooking.getRestaurantTables().stream().findFirst().orElseThrow().getId());
        assertEquals((short) 3, persistedBooking.getNumOfPpl());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @Transactional
    @Rollback
    @Order(4)
    public void shouldSaveBookingForTwoTables() throws Exception {
        Booking booking = createBooking(
                LocalDate.now().plusDays(5),
                LocalTime.of(15, 0),
                "Cieślak",
                List.of(6, 7),
                (short) 7);

        apiRequestUtils.postAndExpect200("/api/restaurant/bookings", booking);

        Booking persistedBooking =
                apiRequestUtils.postObjectExpect200("/api/restaurant/bookings/show", 4L, Booking.class);

        assertEquals(LocalDate.now().plusDays(5), persistedBooking.getDate());
        assertEquals(LocalTime.of(15, 0), persistedBooking.getTime());
        assertEquals("Cieślak", persistedBooking.getSurname());
        assertEquals(2, persistedBooking.getRestaurantTables().size());
        assertEquals(6, persistedBooking.getRestaurantTables().stream().findFirst().orElseThrow().getId());
        assertEquals(7, persistedBooking.getRestaurantTables().stream().skip(1).findFirst().orElseThrow().getId());
        assertEquals((short) 7, persistedBooking.getNumOfPpl());
    }

    @Test
    @WithMockUser(roles = "COOK")
    @Transactional
    @Rollback
    @Order(5)
    public void shouldDeleteBooking() throws Exception {
        Booking existingBooking =
                apiRequestUtils.postObjectExpect200("/api/restaurant/bookings/show", 2L, Booking.class);
        assertNotNull(existingBooking);

        apiRequestUtils.deleteAndExpect200("/api/restaurant/bookings/delete", 2L);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/bookings/show", 2L, status().isBadRequest());
        assertEquals("Rezerwacja z podanym ID = 2 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = "COOK")
    public void shouldGetByDate() throws Exception {
        Map<String, Object> requestParams = getPageableAndDateRanges();
        Page<Booking> bookings =
                apiRequestUtils.fetchAsPage("/api/restaurant/bookings/date", requestParams, Booking.class);

        assertEquals(2, bookings.getTotalElements());
        assertEquals("Pierwszy", bookings.getContent().get(0).getSurname());
        assertEquals("Drugi", bookings.getContent().get(1).getSurname());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldCountAll() throws Exception {
        Long count = apiRequestUtils.fetchObject("/api/restaurant/bookings/count-all", Long.class);
        assertEquals(2L, count);
    }

    @Test
    @WithMockUser(roles = "COOK")
    public void shouldCountByDateBetween() throws Exception {
        Map<String, LocalDate> requestParams =
                Map.of("dateFrom", LocalDate.of(2024, 2, 23),
                        "dateTo", LocalDate.of(2024, 2, 25));
        Long count =
                apiRequestUtils.postAndFetchObject(
                        "/api/restaurant/bookings/count-dates", requestParams, Long.class);

        assertEquals(1, count);
    }

    @Test
    public void shouldNotAllowAccessWithoutAuthorization() throws Exception {
        Booking booking = createBooking(
                LocalDate.now().plusDays(2),
                LocalTime.of(14, 0),
                "Karagor",
                List.of(14),
                (short) 2);
        Map<String, Object> requestParams = getPageableAndDateRanges();

        apiRequestUtils.postAndExpect("/api/restaurant/bookings/show", 1, status().isUnauthorized());
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/bookings", booking);
        apiRequestUtils.deleteAndExpect("/api/restaurant/bookings/delete", 3L, status().isUnauthorized());
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/bookings/date", requestParams);
        apiRequestUtils.fetchAndExpectUnauthorized("/api/restaurant/bookings/count-all");
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/bookings/count-dates", requestParams);
    }

    private Booking createBooking(LocalDate date,
                                  LocalTime time,
                                  String surname,
                                  List<Integer> tableNumbers,
                                  short numOfPpl) {
        Booking booking = new Booking();
        booking.setDate(date);
        booking.setTime(time);
        booking.setSurname(surname);

        Set<RestaurantTable> restaurantTables = new HashSet<>();
        for (Integer tableNum : tableNumbers) {
            RestaurantTable restaurantTable;
            try {
                restaurantTable = restaurantTableService.findById(tableNum);
            } catch (LocalizedException e) {
                throw new RuntimeException(e);
            }
            restaurantTables.add(restaurantTable);
        }

        booking.setRestaurantTables(restaurantTables);
        booking.setNumOfPpl(numOfPpl);
        return booking;
    }

    private Map<String, Object> getPageableAndDateRanges() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("pageNumber", 0);
        requestParams.put("pageSize", 20);
        requestParams.put("dateFrom", LocalDate.of(2024, 2, 23).toString());
        requestParams.put("dateTo", LocalDate.of(2024, 2, 28).toString());
        return requestParams;
    }
}