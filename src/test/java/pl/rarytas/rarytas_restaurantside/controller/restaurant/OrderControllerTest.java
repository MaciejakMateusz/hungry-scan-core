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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiRequestUtils;
import pl.rarytas.rarytas_restaurantside.test_utils.OrderProcessor;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.math.BigDecimal;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderProcessor orderProcessor;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(1)
    public void shouldGetAllOrders() throws Exception {
        List<Order> orders = apiRequestUtils.fetchAsList("/api/restaurant/orders", Order.class);
        assertFalse(orders.stream().anyMatch(Order::isResolved));
        assertEquals(5, orders.size());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToOrders() throws Exception {
        mockMvc.perform(get("/api/restaurant/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(2)
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
    @org.junit.jupiter.api.Order(3)
    public void shouldGetAllDineInOrders() throws Exception {
        List<Order> orders = apiRequestUtils.fetchAsList("/api/restaurant/orders/dine-in", Order.class);
        assertFalse(orders.stream().anyMatch(Order::isForTakeAway));
        assertEquals(4, orders.size());
    }

    @Test
    public void shouldNotAllowUnauthorizedAccessToDineInOrders() throws Exception {
        mockMvc.perform(get("/api/restaurant/orders/dine-in"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(4)
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
    @org.junit.jupiter.api.Order(5)
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
    @org.junit.jupiter.api.Order(6)
    public void shouldSaveNewDineInOrder() throws Exception {
        Order order = orderProcessor.createDineInOrder(15, List.of(4, 12, 15));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order);

        Order persistedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 6, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(15, order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()), persistedOrder.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(7)
    public void shouldNotSaveOrderForOccupiedTable() throws Exception {
        Order order = orderProcessor.createDineInOrder(12, List.of(5, 1, 22));

        Map<?, ?> errors =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/orders/dine-in", order, status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Stolik posiada już zamówienie.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(8)
    public void shouldSaveNewTakeAwayOrder() throws Exception {
        Order order = orderProcessor.createTakeAwayOrder(List.of(3, 10, 22, 33));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/take-away", order);

        Order persistedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 7, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(order.getRestaurantTable().getId(), persistedOrder.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()), persistedOrder.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(9)
    public void shouldSaveNextDineInOrder() throws Exception {
        Order order = orderProcessor.createDineInOrder(10, List.of(7, 9, 22, 31));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order);

        Order persistedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 8, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(persistedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()), persistedOrder.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(10)
    public void shouldRequestBill() throws Exception {
        Order existingOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1L, Order.class);
        assertFalse(existingOrder.isBillRequested());
        assertNull(existingOrder.getPaymentMethod());

        apiRequestUtils.patchAndExpect("/api/restaurant/orders/request-bill", 1L, "cash", status().isOk());

        Order updatedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1L, Order.class);
        assertTrue(updatedOrder.isBillRequested());
        assertEquals("cash", updatedOrder.getPaymentMethod());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(11)
    public void shouldNotRequestBillSecondTime() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 2L, Order.class);
        assertNotNull(order);

        Map<?, ?> errors =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/restaurant/orders/request-bill", 2L, "card", status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Zamówienie z podanym ID = 2 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
                errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(12)
    public void shouldNotCallWaiterWithBillRequested() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 2L, Order.class);
        assertNotNull(order);

        Map<?, ?> errors =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/restaurant/orders/call-waiter", 2L, status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Zamówienie z podanym ID = 2 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
                errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(13)
    public void shouldOrderMoreDishes() throws Exception {
        Order moreDishes = orderProcessor.createDineInOrder(12, List.of(2, 35));
        moreDishes.setId(1L);

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders", moreDishes);

        Order updatedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1L, Order.class);
        assertNotNull(updatedOrder);

        BigDecimal newItemsAmount =
                orderProcessor.countTotalAmount(moreDishes.getOrderedItems());
        BigDecimal newTotalAmount = updatedOrder.getTotalAmount();

        assertEquals(Money.of(40.00), newItemsAmount);
        assertEquals(Money.of(84.00), newTotalAmount);
        assertEquals(3, updatedOrder.getOrderedItems().size());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(14)
    public void shouldCallWaiter() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1, Order.class);
        assertFalse(order.isWaiterCalled());

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders/call-waiter", 1L);

        Order updatedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1, Order.class);
        assertTrue(updatedOrder.isWaiterCalled());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(15)
    public void shouldNotRequestBillWhenWaiterCalled() throws Exception {
        Map<?, ?> errors =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/restaurant/orders/request-bill", 3L, "card", status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Zamówienie z podanym ID = 3 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
                errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(16)
    public void shouldNotCallWaiterSecondTime() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 3, Order.class);
        assertNotNull(order);

        Map<?, ?> errors =
                apiRequestUtils.patchAndReturnResponseBody(
                        "/api/restaurant/orders/call-waiter", 3L, status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Zamówienie z podanym ID = 3 posiada aktywne wezwanie kelnera lub prośbę o rachunek.",
                errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(17)
    public void shouldResolveWaiterCall() throws Exception {
        Order existingOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 3, Order.class);
        assertTrue(existingOrder.isWaiterCalled());

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders/resolve-call", 3L);
        Order updatedOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 3, Order.class);

        assertFalse(updatedOrder.isWaiterCalled());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(18)
    public void shouldFinalizeDineIn() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 2, Order.class);
        assertTrue(order.isBillRequested());

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/finalize-dine-in", 2);

        Map<?, ?> errors =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/orders/show", 2L, status().isBadRequest());
        assertEquals("Zamówienie z podanym ID = 2 nie istnieje.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(19)
    public void shouldFinalizeTakeAway() throws Exception {
        Order order =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 4L, Order.class);
        assertTrue(order.isForTakeAway());

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/finalize-take-away", 4L);

        Map<?, ?> errors =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/orders/finalize-take-away", 4L, status().isBadRequest());
        assertEquals("Zamówienie z podanym ID = 4 nie istnieje.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(20)
    public void shouldGiveTip() throws Exception {
        Order existingOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1L, Order.class);
        assertEquals(Money.of(44.00), existingOrder.getTotalAmount());
        assertEquals(Money.of(0.00), existingOrder.getTipAmount());

        BigDecimal tipAmount = Money.of(50.00);
        apiRequestUtils.patchAndExpect(
                "/api/restaurant/orders/tip", 1L, tipAmount, status().isOk());

        existingOrder =
                apiRequestUtils.postObjectExpect200("/api/restaurant/orders/show", 1L, Order.class);
        assertEquals(Money.of(94.00), existingOrder.getTotalAmount());
        assertEquals(tipAmount, existingOrder.getTipAmount());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(21)
    public void shouldNotGiveZeroTip() throws Exception {
        BigDecimal tipAmount = Money.of(0.00);
        Map<?, ?> errors = apiRequestUtils.patchAndReturnResponseBody(
                "/api/restaurant/orders/tip", 1L, tipAmount, status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Wysokość napiwku musi być większa od 0.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    @Transactional
    @Rollback
    @org.junit.jupiter.api.Order(22)
    public void shouldNotGiveNegativeTip() throws Exception {
        BigDecimal tipAmount = Money.of(-50.00);
        Map<?, ?> errors = apiRequestUtils.patchAndReturnResponseBody(
                "/api/restaurant/orders/tip", 1L, tipAmount, status().isBadRequest());

        assertEquals(1, errors.size());
        assertEquals("Wysokość napiwku musi być większa od 0.", errors.get("exceptionMsg"));
    }

    @Test
    public void shouldNotAllowAccessWithoutAuthorization() throws Exception {
        Order order = orderProcessor.createDineInOrder(12, List.of(4, 12, 15));
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/orders/dine-in", order);
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/orders/take-away", order);
        apiRequestUtils.patchAndExpect(
                "/api/restaurant/orders/request-bill", 6L, "cash", status().isUnauthorized());
        apiRequestUtils.patchAndExpect(
                "/api/restaurant/orders/tip", 6L, Money.of(15.00), status().isUnauthorized());
        apiRequestUtils.patchAndExpectUnauthorized("/api/restaurant/orders/call-waiter", 111L);
        apiRequestUtils.patchAndExpectUnauthorized("/api/restaurant/orders/resolve-call", 111L);
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/orders/finalize-dine-in", 111L);
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/orders/finalize-take-away", 111L);
    }
}