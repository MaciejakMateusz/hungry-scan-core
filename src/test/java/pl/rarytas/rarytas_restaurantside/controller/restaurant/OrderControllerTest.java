package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.testSupport.ApiRequestUtils;
import pl.rarytas.rarytas_restaurantside.testSupport.OrderProcessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderProcessor orderProcessor;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetAllOrders() throws Exception {
        List<Order> orders = apiRequestUtils.fetchAsList("/api/restaurant/orders", Order.class);
        assertFalse(orders.stream().anyMatch(Order::isPaid));
        assertEquals(3, orders.size());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToOrders() throws Exception {
        mockMvc.perform(get("/api/restaurant/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetAllTakeAwayOrders() throws Exception {
        List<Order> orders = apiRequestUtils.fetchAsList("/api/restaurant/orders/take-away", Order.class);
        assertFalse(orders.stream().anyMatch(order -> !order.isForTakeAway()));
        assertEquals(1, orders.size());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToTakeAwayOrders() throws Exception {
        mockMvc.perform(get("/api/restaurant/orders/take-away"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetAllDineInOrders() throws Exception {
        List<Order> orders = apiRequestUtils.fetchAsList("/api/restaurant/orders/dine-in", Order.class);
        assertFalse(orders.stream().anyMatch(Order::isForTakeAway));
        assertEquals(3, orders.size());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToDineInOrders() throws Exception {
        mockMvc.perform(get("/api/restaurant/orders/dine-in"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetByTableNumber() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/table-number", 2, Order.class);
        assertEquals("cash", order.getPaymentMethod());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToOrderByTableNumber() throws Exception {
        mockMvc.perform(post("/api/restaurant/orders/table-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    public void shouldGetById() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 2, Order.class);
        assertEquals(322, order.getOrderNumber());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToOrderById() throws Exception {
        mockMvc.perform(post("/api/restaurant/orders/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    public void launchPostPatchTestsInSequence() throws Exception {
        shouldSaveNewDineInOrder();
        shouldNotSaveOrderForOccupiedTable();
        shouldSaveNewTakeAwayOrder();
        shouldSaveNextDineInOrder();
        shouldRequestBillAndUpdateOrder();
        shouldNotRequestBillSecondTime();
        shouldNotCallWaiterWithBillRequested();
        shouldOrderMoreDishes();
        shouldCallWaiter();
        shouldNotRequestBillWhenWaiterCalled();
        shouldNotCallWaiterSecondTime();
        shouldResolveWaiterCall();
        shouldFinalizeDineIn();
        shouldFinalizeTakeAway();
    }

    private void shouldSaveNewDineInOrder() throws Exception {
        Order order = orderProcessor.createDineInOrder(12, List.of(4, 12, 15));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order);

        Order persistedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 5, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(persistedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(persistedOrder.getOrderedItems()));
    }

    private void shouldNotSaveOrderForOccupiedTable() throws Exception {
        Order order = orderProcessor.createDineInOrder(12, List.of(5, 1, 22));

        Map<?, ?> errors =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/orders/dine-in", order, status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Stolik posiada już zamówienie.", errors.get("exceptionMsg"));
    }

    public void shouldSaveNewTakeAwayOrder() throws Exception {
        Order order = orderProcessor.createTakeAwayOrder(List.of(3, 10, 22, 33));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/take-away", order);

        Order persistedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 6, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(persistedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(persistedOrder.getOrderedItems()));
    }

    private void shouldSaveNextDineInOrder() throws Exception {
        Order order = orderProcessor.createDineInOrder(10, List.of(7, 9, 22, 31));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order);

        Order persistedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 7, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(persistedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(persistedOrder.getOrderedItems()));
    }

    private void shouldRequestBillAndUpdateOrder() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 5, Order.class);
        assertNotNull(order);

        apiRequestUtils.patchAndExpect(
                "/api/restaurant/orders/request-bill", 5L, "cash", status().isOk());

        Order updatedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 5, Order.class);
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.isBillRequested());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(updatedOrder.getOrderedItems()));
    }

    private void shouldNotRequestBillSecondTime() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 5, Order.class);
        assertNotNull(order);

        Map<?, ?> errors =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/restaurant/orders/request-bill", 5L, "card", status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Zamówienie z podanym ID = 5 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
                errors.get("exceptionMsg"));
    }

    private void shouldNotCallWaiterWithBillRequested() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 5, Order.class);
        assertNotNull(order);

        Map<?, ?> errors =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/restaurant/orders/call-waiter", 5L, status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Zamówienie z podanym ID = 5 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
                errors.get("exceptionMsg"));
    }

    private void shouldOrderMoreDishes() throws Exception {
        Order moreDishes = orderProcessor.createDineInOrder(12, List.of(2, 35));
        moreDishes.setId(7L);

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders", moreDishes);

        Order updatedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertNotNull(updatedOrder);

        BigDecimal newItemsAmount = orderProcessor.countTotalAmount(moreDishes.getOrderedItems()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newTotalAmount = orderProcessor.countTotalAmount(updatedOrder.getOrderedItems()).setScale(2, RoundingMode.HALF_UP);

        assertEquals(BigDecimal.valueOf(40.00).setScale(2, RoundingMode.HALF_UP), newItemsAmount);
        assertEquals(BigDecimal.valueOf(136.25).setScale(2, RoundingMode.HALF_UP), newTotalAmount);
        assertEquals(6, updatedOrder.getOrderedItems().size());
    }

    private void shouldCallWaiter() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertFalse(order.isWaiterCalled());

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders/call-waiter", 7L);

        Order updatedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertTrue(updatedOrder.isWaiterCalled());
    }

    private void shouldNotRequestBillWhenWaiterCalled() throws Exception {
        Map<?, ?> errors =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/restaurant/orders/request-bill", 7L, "card", status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Zamówienie z podanym ID = 7 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
                errors.get("exceptionMsg"));
    }

    private void shouldNotCallWaiterSecondTime() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertNotNull(order);

        Map<?, ?> errors =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/restaurant/orders/call-waiter", 7L, status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Zamówienie z podanym ID = 7 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
                errors.get("exceptionMsg"));
    }

    private void shouldResolveWaiterCall() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/orders/resolve-call", 7L);
        Order updatedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 7, Order.class);

        assertFalse(updatedOrder.isWaiterCalled());
    }

    private void shouldFinalizeDineIn() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertNotNull(order);

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/finalize-dine-in", 7);

        Map<?, ?> errors =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/orders/show", 7L, status().isBadRequest());
        assertEquals("Zamówienie z podanym ID = 7 nie istnieje.", errors.get("exceptionMsg"));
    }

    private void shouldFinalizeTakeAway() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 6L, Order.class);
        assertNotNull(order);
        assertTrue(order.isForTakeAway());

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/finalize-take-away", 6L);

        Map<?, ?> errors =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/orders/finalize-take-away", 6L, status().isBadRequest());
        assertEquals("Zamówienie z podanym ID = 6 nie istnieje.", errors.get("exceptionMsg"));
    }

    @Test
    public void shouldNotAllowAccessWithoutAuthorization() throws Exception {
        Order order = orderProcessor.createDineInOrder(12, List.of(4, 12, 15));
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/orders/dine-in", order);
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/orders/take-away", order);
        apiRequestUtils.patchAndExpect("/api/restaurant/orders/request-bill", 6L, "cash", status().isUnauthorized());
        apiRequestUtils.patchAndExpectUnauthorized("/api/restaurant/orders/call-waiter", 111L);
        apiRequestUtils.patchAndExpectUnauthorized("/api/restaurant/orders/resolve-call", 111L);
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/orders/finalize-dine-in", 111L);
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/orders/finalize-take-away", 111L);
    }
}