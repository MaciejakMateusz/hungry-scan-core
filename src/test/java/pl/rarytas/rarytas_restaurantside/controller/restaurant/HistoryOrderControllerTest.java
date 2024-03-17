package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HistoryOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(1)
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
    @org.junit.jupiter.api.Order(2)
    public void shouldNotAllowUnauthorizedAccessToDineInHistoryOrders() {
        Map<String, Object> requestParams = Map.of("pageNumber", 0, "pageSize", 20);
        assertThrows(AssertionError.class, () -> apiRequestUtils.fetchAsPage(
                "/api/restaurant/history-orders/dine-in", requestParams, HistoryOrder.class));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(3)
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
    @org.junit.jupiter.api.Order(4)
    public void shouldNotAllowUnauthorizedAccessToTakeAwayOrders() {
        Map<String, Object> requestParams = Map.of("pageNumber", 0, "pageSize", 20);

        assertThrows(AssertionError.class, () -> apiRequestUtils.fetchAsPage(
                "/api/restaurant/history-orders/take-away", requestParams, HistoryOrder.class));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(5)
    public void shouldCountAll() throws Exception {
        Long count = apiRequestUtils.fetchObject("/api/restaurant/history-orders/count", Long.class);
        assertEquals(4L, count);
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    public void shouldNotAllowUnauthorizedAccessToCountAll() {
        assertThrows(AssertionError.class, () ->
                apiRequestUtils.fetchObject("/api/restaurant/history-orders/count", Long.class));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(7)
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
    @org.junit.jupiter.api.Order(8)
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
    @org.junit.jupiter.api.Order(10)
    public void shouldGetById() throws Exception {
        HistoryOrder historyOrder =
                apiRequestUtils.postObjectExpect200(
                        "/api/restaurant/history-orders/show", 13, HistoryOrder.class);
        assertEquals("cash", historyOrder.getPaymentMethod());
    }

    @Test
    @org.junit.jupiter.api.Order(11)
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