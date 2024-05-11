package pl.rarytas.hungry_scan_core.controller.restaurant.history;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.hungry_scan_core.entity.history.HistoryOrder;
import pl.rarytas.hungry_scan_core.test_utils.ApiRequestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HistoryOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetDineInHistoryOrders() throws Exception {
        Map<String, Object> requestParams = Map.of("pageNumber", 0, "pageSize", 20);
        Page<HistoryOrder> historyOrders =
                apiRequestUtils.fetchAsPage(
                        "/api/restaurant/history-orders/dine-in", requestParams, HistoryOrder.class);

        assertTrue(historyOrders.stream().anyMatch(HistoryOrder::isResolved));
        assertFalse(historyOrders.stream().anyMatch(HistoryOrder::isForTakeAway));
        assertEquals(2, historyOrders.getTotalElements());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToDineInHistoryOrders() {
        Map<String, Object> requestParams = Map.of("pageNumber", 0, "pageSize", 20);
        assertThrows(AssertionError.class, () -> apiRequestUtils.fetchAsPage(
                "/api/restaurant/history-orders/dine-in", requestParams, HistoryOrder.class));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetAllTakeAwayHistoryOrders() throws Exception {
        Map<String, Object> requestParams = Map.of("pageNumber", 0, "pageSize", 20);
        Page<HistoryOrder> historyOrders =
                apiRequestUtils.fetchAsPage(
                        "/api/restaurant/history-orders/take-away", requestParams, HistoryOrder.class);

        assertTrue(historyOrders.stream().anyMatch(HistoryOrder::isResolved));
        assertTrue(historyOrders.stream().anyMatch(HistoryOrder::isForTakeAway));
        assertEquals(2, historyOrders.getTotalElements());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToTakeAwayOrders() {
        Map<String, Object> requestParams = Map.of("pageNumber", 0, "pageSize", 20);

        assertThrows(AssertionError.class, () -> apiRequestUtils.fetchAsPage(
                "/api/restaurant/history-orders/take-away", requestParams, HistoryOrder.class));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldCountAll() throws Exception {
        Long count = apiRequestUtils.fetchObject("/api/restaurant/history-orders/count", Long.class);
        assertEquals(4L, count);
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToCountAll() {
        assertThrows(AssertionError.class, () ->
                apiRequestUtils.fetchObject("/api/restaurant/history-orders/count", Long.class));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetDineInByDate() throws Exception {
        Map<String, Object> requestParams = getPageableAndDateRanges();

        Page<HistoryOrder> historyOrders =
                apiRequestUtils.fetchAsPage(
                        "/api/restaurant/history-orders/dine-in/date", requestParams, HistoryOrder.class);

        assertTrue(historyOrders.stream().anyMatch(HistoryOrder::isResolved));
        assertFalse(historyOrders.stream().anyMatch(HistoryOrder::isForTakeAway));
        assertEquals(1, historyOrders.getTotalElements());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetTakeAwayByDate() throws Exception {
        Map<String, Object> requestParams = getPageableAndDateRanges();
        Page<HistoryOrder> historyOrders =
                apiRequestUtils.fetchAsPage(
                        "/api/restaurant/history-orders/take-away/date", requestParams, HistoryOrder.class);

        assertTrue(historyOrders.stream().anyMatch(HistoryOrder::isResolved));
        assertTrue(historyOrders.stream().anyMatch(HistoryOrder::isForTakeAway));
        assertEquals(1, historyOrders.getTotalElements());
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    public void shouldNotAllowUnauthorizedAccessToHistoryOrdersByDate() {
        Map<String, Object> requestParams = getPageableAndDateRanges();

        assertThrows(AssertionError.class, () -> apiRequestUtils.fetchAsPage(
                "/api/restaurant/history-orders/dine-in/date", requestParams, HistoryOrder.class));

        assertThrows(AssertionError.class, () -> apiRequestUtils.fetchAsPage(
                "/api/restaurant/history-orders/take-away/date", requestParams, HistoryOrder.class));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetById() throws Exception {
        HistoryOrder historyOrder =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/history-orders/show", 13, HistoryOrder.class);
        assertNotNull(historyOrder);
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToHistoryOrderById() throws Exception {
        mockMvc.perform(post("/api/restaurant/history-orders/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isUnauthorized());
    }

    private Map<String, Object> getPageableAndDateRanges() {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("pageNumber", 0);
        requestParams.put("pageSize", 20);
        requestParams.put("dateFrom", "2024-02-20");
        requestParams.put("dateTo", "2024-02-22");
        return requestParams;
    }
}