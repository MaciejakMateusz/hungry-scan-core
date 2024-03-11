package pl.rarytas.rarytas_restaurantside.controller.restaurant;

import jakarta.servlet.ServletException;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderProcessor orderProcessor;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(1)
    public void shouldGetAllOrders() throws Exception {
        List<Order> orders = apiRequestUtils.fetchObjects("/api/restaurant/orders", Order.class);
        assertFalse(orders.stream().anyMatch(Order::isPaid));
        assertEquals(3, orders.size());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void shouldNotAllowUnauthorizedAccessToOrders() throws Exception {
        mockMvc.perform(get("/api/restaurant/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(3)
    public void shouldGetAllTakeAwayOrders() throws Exception {
        List<Order> orders = apiRequestUtils.fetchObjects("/api/restaurant/orders/take-away", Order.class);
        assertFalse(orders.stream().anyMatch(order -> !order.isForTakeAway()));
        assertEquals(1, orders.size());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void shouldNotAllowUnauthorizedAccessToTakeAwayOrders() throws Exception {
        mockMvc.perform(get("/api/restaurant/orders/take-away"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(5)
    public void shouldGetAllDinInOrders() throws Exception {
        List<Order> orders = apiRequestUtils.fetchObjects("/api/restaurant/orders/dine-in", Order.class);
        assertFalse(orders.stream().anyMatch(Order::isForTakeAway));
        assertEquals(3, orders.size());
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    public void shouldNotAllowUnauthorizedAccessToDineInOrders() throws Exception {
        mockMvc.perform(get("/api/restaurant/orders/dine-in"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(7)
    public void shouldGetByTableNumber() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/table-number", 2, Order.class);
        assertEquals("cash", order.getPaymentMethod());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    public void shouldNotAllowUnauthorizedAccessToOrderByTableNumber() throws Exception {
        mockMvc.perform(post("/api/restaurant/orders/table-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "WAITER")
    @org.junit.jupiter.api.Order(9)
    public void shouldGetById() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 2, Order.class);
        assertEquals(322, order.getOrderNumber());
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    public void shouldNotAllowUnauthorizedAccessToOrderById() throws Exception {
        mockMvc.perform(post("/api/restaurant/orders/show")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(roles = {"WAITER"})
    @org.junit.jupiter.api.Order(11)
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
    }

    private void shouldSaveNewDineInOrder() throws Exception {
        Order order = orderProcessor.createDineInOrder(12, List.of(4, 12, 15));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order);

        Order persistedOrder =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 5, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(persistedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(persistedOrder.getOrderedItems()));
    }

    private void shouldNotSaveOrderForOccupiedTable() throws Exception {
        Order order = orderProcessor.createDineInOrder(12, List.of(5, 1, 22));

        assertThrows(ServletException.class, () ->
                apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order));
    }

    public void shouldSaveNewTakeAwayOrder() throws Exception {
        Order order = orderProcessor.createTakeAwayOrder(19, List.of(3, 10, 22, 33));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/take-away", order);

        Order persistedOrder =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 6, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(persistedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(persistedOrder.getOrderedItems()));
    }

    private void shouldSaveNextDineInOrder() throws Exception {
        Order order = orderProcessor.createDineInOrder(10, List.of(7, 9, 22, 31));

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/dine-in", order);

        Order persistedOrder =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 7, Order.class);

        assertNotNull(persistedOrder);
        assertEquals(persistedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(persistedOrder.getOrderedItems()));
    }

    private void shouldRequestBillAndUpdateOrder() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 5, Order.class);
        assertNotNull(order);

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders/request-bill", order);

        Order updatedOrder =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 5, Order.class);
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.isBillRequested());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(updatedOrder.getOrderedItems()));
    }

    private void shouldNotRequestBillSecondTime() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 5, Order.class);
        assertNotNull(order);
        assertThrows(ServletException.class, () ->
                apiRequestUtils.patchAndExpect200("/api/restaurant/orders/request-bill", order)
        );
    }

    private void shouldNotCallWaiterWithBillRequested() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 5, Order.class);
        assertNotNull(order);
        assertThrows(ServletException.class, () ->
                apiRequestUtils.patchAndExpect200("/api/restaurant/orders/call-waiter", order)
        );
    }

    private void shouldOrderMoreDishes() throws Exception {
        Order moreDishes = orderProcessor.createDineInOrder(12, List.of(2, 35));
        moreDishes.setId(7L);

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders", moreDishes);

        Order updatedOrder =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertNotNull(updatedOrder);

        BigDecimal newItemsAmount = orderProcessor.countTotalAmount(moreDishes.getOrderedItems()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newTotalAmount = orderProcessor.countTotalAmount(updatedOrder.getOrderedItems()).setScale(2, RoundingMode.HALF_UP);

        assertEquals(BigDecimal.valueOf(40.00).setScale(2, RoundingMode.HALF_UP), newItemsAmount);
        assertEquals(BigDecimal.valueOf(136.25).setScale(2, RoundingMode.HALF_UP), newTotalAmount);
        assertEquals(6, updatedOrder.getOrderedItems().size());
    }

    private void shouldCallWaiter() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertNotNull(order);

        apiRequestUtils.patchAndExpect200("/api/restaurant/orders/call-waiter", order);

        Order updatedOrder = orderService.findById(7L);
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.isWaiterCalled());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(updatedOrder.getOrderedItems()));
    }

    private void shouldNotRequestBillWhenWaiterCalled() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertNotNull(order);
        assertThrows(ServletException.class, () ->
                apiRequestUtils.patchAndExpect200("/api/restaurant/orders/request-bill", order));
    }

    private void shouldNotCallWaiterSecondTime() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertNotNull(order);
        assertThrows(Exception.class, () ->
                apiRequestUtils.patchAndExpect200("/api/restaurant/orders/call-waiter", order));
    }

    private void shouldResolveWaiterCall() throws Exception {
        apiRequestUtils.patchAndExpect200("/api/restaurant/orders/resolve-call", 7L);
        Order updatedOrder =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 7, Order.class);

        assertFalse(updatedOrder.isWaiterCalled());
    }

    private void shouldFinalizeDineIn() throws Exception {
        Order order =
                apiRequestUtils.getObjectExpect200("/api/restaurant/orders/show", 7, Order.class);
        assertNotNull(order);

        apiRequestUtils.postAndExpect200("/api/restaurant/orders/finalize-dine-in", 7);

        Map<?, ?> errors =
                apiRequestUtils.postAndReturnResponseBody(
                        "/api/restaurant/orders/show", 7L, status().isBadRequest());
        assertEquals("Zamówienie z podanym ID = 7 nie istnieje.", errors.get("exceptionMsg"));
    }
}