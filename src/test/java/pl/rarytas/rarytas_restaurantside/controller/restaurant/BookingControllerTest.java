package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = "WAITER")
    @Order(1)
    public void shouldGetById() throws Exception {
        Booking booking =
                apiRequestUtils.postObjectExpect200("/api/restaurant/bookings/show", 1, Booking.class);
        assertEquals("Pierwszy", booking.getSurname());
    }

    @Test
    @Order(2)
    public void shouldNotAllowUnauthorizedAccessToBookingById() throws Exception {
        apiRequestUtils.postAndExpect("/api/restaurant/bookings/show", 1, status().isUnauthorized());
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "COOK")
    public void testInSequence() throws Exception {
        shouldSaveNewBooking(2L);
        shouldSaveNewBooking(3L);
        shouldSaveNewBooking(4L);
        shouldSaveNewBooking(5L);
        shouldDeleteBooking();
        shouldGetByDate();
        shouldCountAll();
        shouldCountByDateBetween();
    }

    private void shouldSaveNewBooking(Long daysToAdd) throws Exception {
        Booking booking = createBooking(daysToAdd);
        apiRequestUtils.postAndExpect200("/api/restaurant/bookings", booking);

        //to simplify, "daysToAdd" will represent ID of booking to fetch in this request
        Booking persistedBooking =
                apiRequestUtils.postObjectExpect200("/api/restaurant/bookings/show", daysToAdd, Booking.class);

        assertEquals("TestSurname" + daysToAdd, persistedBooking.getSurname());
    }

    private void shouldDeleteBooking() throws Exception {
        apiRequestUtils.deleteAndExpect200("/api/restaurant/bookings/delete", 5L);

        Map<String, Object> responseBody =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/bookings/show", 5L, status().isBadRequest());
        assertEquals("Rezerwacja z podanym ID = 5 nie istnieje.", responseBody.get("exceptionMsg"));
    }

    private void shouldGetByDate() throws Exception {
        Map<String, Object> requestParams = getPageableAndDateRanges();
        Page<Booking> bookings =
                apiRequestUtils.fetchAsPage("/api/restaurant/bookings/date", requestParams, Booking.class);

        assertEquals(3, bookings.getTotalElements());
    }

    private void shouldCountAll() throws Exception {
        Long count = apiRequestUtils.fetchObject("/api/restaurant/bookings/count-all", Long.class);
        assertEquals(4L, count);
    }

    private void shouldCountByDateBetween() throws Exception {
        Map<String, LocalDate> requestParams =
                Map.of("dateFrom", LocalDate.now(), "dateTo", LocalDate.now().plusDays(2));
        Long count =
                apiRequestUtils.postAndFetchObject(
                        "/api/restaurant/bookings/count-dates", requestParams, Long.class);

        assertEquals(1, count);
    }

    @Test
    @Order(4)
    public void shouldNotAllowAccessWithoutAuthorization() throws Exception {
        Booking booking = createBooking(12L);
        Map<String, Object> requestParams = getPageableAndDateRanges();
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/bookings", booking);
        apiRequestUtils.deleteAndExpect("/api/restaurant/bookings/delete", 3L, status().isUnauthorized());
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/bookings/date", requestParams);
        apiRequestUtils.fetchAndExpectUnauthorized("/api/restaurant/bookings/count-all");
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/bookings/count-dates", requestParams);
    }

    private Booking createBooking(Long daysToAdd) {
        Booking booking = new Booking();
        booking.setDate(LocalDate.now().plusDays(daysToAdd));
        booking.setTime(LocalTime.of(14, 0));
        booking.setSurname("TestSurname" + daysToAdd);
        booking.setTableId(14);
        booking.setNumOfPpl((short) 2);
        return booking;
    }

    private Map<String, Object> getPageableAndDateRanges() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("pageNumber", 0);
        requestParams.put("pageSize", 20);
        requestParams.put("dateFrom", LocalDate.now().toString());
        requestParams.put("dateTo", LocalDate.now().plusDays(6L).toString());
        return requestParams;
    }
}