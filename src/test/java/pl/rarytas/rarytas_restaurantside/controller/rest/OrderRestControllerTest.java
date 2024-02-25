package pl.rarytas.rarytas_restaurantside.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.testSupport.OrderProcessor;

import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderProcessor orderProcessor;

    @Test
    @org.junit.jupiter.api.Order(1)
    public void shouldGetAllNotPaidFromDB() {
        List<Order> orders = orderService.findAllNotPaid();

        //checking if orders list does not contain order that is paid
        assertFalse(orders.stream().anyMatch(Order::isPaid));

        //data-h2.sql file contain only 3 order inserts that are not paid
        assertEquals(3, orders.size());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void shouldGetAllNotPaidFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertFalse(actualOrderJson.contains("\"paid\":true"));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void shouldGetAllResolvedFromDB() {
        List<Order> orders = orderService.findAllByResolvedIsTrue();

        //checking if orders list contain only resolved orders
        assertTrue(orders.stream().allMatch(Order::isResolved));

        //data-h2.sql file contain only 2 order inserts that are resolved
        assertEquals(2, orders.size());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void shouldGetAllResolvedFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/resolved")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertFalse(actualOrderJson.contains("\"resolved\":false"));
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    public void shouldGetFinalizedDineInByIdFromDB() {
        Order order = orderService.findFinalizedById(3, false).orElse(new Order());

        assertTrue(order.isPaid());
        assertTrue(order.isResolved());
        assertFalse(order.isForTakeAway());
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    public void shouldGetFinalizedDineInByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/finalized/id/3/false")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertFalse(actualOrderJson.contains("\"resolved\":false"));
        assertFalse(actualOrderJson.contains("\"paid\":false"));
        assertFalse(actualOrderJson.contains("\"forTakeAway\":true"));
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    public void shouldGetAllForTakeAwayFromDB() {
        List<Order> orders = orderService.findAllTakeAway();
        assertEquals(1, orders.size());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    public void shouldGetAllForTakeAwayFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/takeAway")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertFalse(actualOrderJson.contains("\"forTakeAway\":false"));
        assertTrue(actualOrderJson.contains("\"paymentMethod\":\"online\""));
        assertFalse(actualOrderJson.contains("\"paymentMethod\":\"null\""));
        assertFalse(actualOrderJson.contains("\"paymentMethod\":\"card\""));
        assertFalse(actualOrderJson.contains("\"paymentMethod\":\"cash\""));
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    public void shouldGetByTableNumberFromDB() {
        Order order = orderService.findByTableNumber(2).orElse(new Order());
        //only table number 2 has cash payment method
        assertEquals("cash", order.getPaymentMethod());
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    public void shouldGetByTableNumberFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/2")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertTrue(actualOrderJson.contains("\"paymentMethod\":\"cash\""));
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    public void shouldGetByIdFromDB() {
        Order order = (Order) orderService.findById(4).orElseThrow();
        //only order with ID 4 has total amount = 73.50
        assertEquals("73.50", order.getTotalAmount().setScale(2, RoundingMode.HALF_UP).toString());
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    public void shouldGetByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/id/4")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertTrue(actualOrderJson.contains("\"totalAmount\":73.50"));
    }

    @Test
    @org.junit.jupiter.api.Order(13)
    public void shouldSaveNewDineInOrder() throws Exception {
        Order order = orderProcessor.getCreatedOrder(12, List.of(4, 12, 15), false);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().is2xxSuccessful());

        Order savedOrder = (Order) orderService.findById(7).orElse(null);
        assertNotNull(savedOrder);
        assertEquals(savedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(savedOrder.getOrderedItems()));
    }

    @Test
    @org.junit.jupiter.api.Order(14)
    public void shouldNotSaveOrderForOccupiedTable() throws Exception {
        Order order = orderProcessor.getCreatedOrder(12, List.of(5, 1, 22), false);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
        );
    }

    @Test
    @org.junit.jupiter.api.Order(15)
    public void shouldSaveNewTakeAwayOrder() throws Exception {
        Order order = orderProcessor.getCreatedOrder(19, List.of(3, 10, 22, 33), true);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().is2xxSuccessful());

        Order savedOrder = (Order) orderService.findById(8).orElse(null);
        assertNotNull(savedOrder);
        assertEquals(savedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(savedOrder.getOrderedItems()));
    }

    @Test
    @org.junit.jupiter.api.Order(16)
    public void shouldSaveNextDineInOrder() throws Exception {
        Order order = orderProcessor.getCreatedOrder(10, List.of(7, 9, 22, 31), false);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().is2xxSuccessful());

        Order savedOrder = (Order) orderService.findById(9).orElse(null);
        assertNotNull(savedOrder);
        assertEquals(savedOrder.getRestaurantTable().getId(), order.getRestaurantTable().getId());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(savedOrder.getOrderedItems()));
    }

    @Test
    @org.junit.jupiter.api.Order(17)
    public void shouldRequestBillAndUpdateOrder() throws Exception {
        Order order = (Order) orderService.findById(7).orElse(null);
        assertNotNull(order);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        mockMvc.perform(patch("/api/orders/request-bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().is2xxSuccessful());

        Order updatedOrder = (Order) orderService.findById(7).orElse(null);
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.isBillRequested());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(updatedOrder.getOrderedItems()));
    }

    @Test
    @org.junit.jupiter.api.Order(18)
    public void shouldThrowWhenRequestingBill() throws Exception {
        Order order = (Order) orderService.findById(7).orElse(null);
        assertNotNull(order);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        assertThrows(Exception.class, () ->
                mockMvc.perform(patch("/api/orders/request-bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson)));
    }

    @Test
    @org.junit.jupiter.api.Order(19)
    public void shouldThrowWhenCallingWaiter() throws Exception {
        Order order = (Order) orderService.findById(7).orElse(null);
        assertNotNull(order);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        assertThrows(Exception.class, () ->
                mockMvc.perform(patch("/api/orders/call-waiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson)));
    }

    @Test
    @org.junit.jupiter.api.Order(20)
    public void shouldCallWaiter() throws Exception {
        Order order = (Order) orderService.findById(9).orElse(null);
        assertNotNull(order);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        mockMvc.perform(patch("/api/orders/call-waiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().is2xxSuccessful());

        Order updatedOrder = (Order) orderService.findById(9).orElse(null);
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.isWaiterCalled());
        assertEquals(orderProcessor.countTotalAmount(order.getOrderedItems()),
                orderProcessor.countTotalAmount(updatedOrder.getOrderedItems()));
    }

    @Test
    @org.junit.jupiter.api.Order(21)
    public void shouldNotRequestBill() throws Exception {
        Order order = (Order) orderService.findById(9).orElse(null);
        assertNotNull(order);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        assertThrows(Exception.class, () ->
                mockMvc.perform(patch("/api/orders/request-bill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson)));
    }

    @Test
    @org.junit.jupiter.api.Order(22)
    public void shouldNotCallWaiter() throws Exception {
        Order order = (Order) orderService.findById(9).orElse(null);
        assertNotNull(order);

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        assertThrows(Exception.class, () ->
                mockMvc.perform(patch("/api/orders/call-waiter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson)));
    }
}