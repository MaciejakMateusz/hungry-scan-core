package pl.rarytas.rarytas_restaurantside.controller.restaurant.history;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryBooking;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HistoryBookingControllerTest {

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetById() throws Exception {
        HistoryBooking booking =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/history-bookings/show", 51, HistoryBooking.class);
        assertEquals("Gibson", booking.getSurname());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToHistoryBookingById() throws Exception {
        apiRequestUtils.postAndExpect(
                "/api/restaurant/history-bookings/show", 51, status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "COOK")
    public void shouldGetByDate() throws Exception {
        Map<String, Object> requestParams = getPageableAndDateRange();
        Page<HistoryBooking> bookings =
                apiRequestUtils.fetchAsPage(
                        "/api/restaurant/history-bookings/date", requestParams, HistoryBooking.class);

        assertEquals(3, bookings.getTotalElements());
    }

    @Test
    @WithMockUser(roles = "COOK")
    public void shouldCountAll() throws Exception {
        Long count = apiRequestUtils.fetchObject("/api/restaurant/history-bookings/count-all", Long.class);
        assertEquals(5L, count);
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldCountByDateBetween() throws Exception {
        Map<String, LocalDate> requestParams =
                Map.of("dateFrom", LocalDate.of(2024, 1, 25),
                        "dateTo", LocalDate.of(2024, 1, 27));
        Long count =
                apiRequestUtils.postAndFetchObject(
                        "/api/restaurant/history-bookings/count-dates", requestParams, Long.class);

        assertEquals(2, count);
    }

    @Test
    public void shouldNotAllowAccessWithoutAuthorization() throws Exception {
        Map<String, Object> requestParams = getPageableAndDateRange();
        apiRequestUtils.fetchAndExpectUnauthorized("/api/restaurant/bookings/count-all");
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/bookings/count-dates", requestParams);
    }

    private Map<String, Object> getPageableAndDateRange() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("pageNumber", 0);
        requestParams.put("pageSize", 20);
        requestParams.put("dateFrom", LocalDate.of(2024, 1, 19).toString());
        requestParams.put("dateTo", LocalDate.of(2024, 1, 23).toString());
        return requestParams;
    }
}