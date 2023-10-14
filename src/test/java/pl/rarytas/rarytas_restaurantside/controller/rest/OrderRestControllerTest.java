package pl.rarytas.rarytas_restaurantside.controller.rest;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderServiceInterface;

import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderServiceInterface orderService;

    @Test
    public void shouldGetAllNotPaidFromDB() {
        List<Order> orders = orderService.findAllNotPaid();

        //checking if orders list does not contain order that is paid
        assertFalse(orders.stream().anyMatch(Order::isPaid));

        //data-h2.sql file contain only 3 order inserts that are not paid
        assertEquals(3, orders.size());
    }

    @Test
    public void shouldGetAllNotPaidFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertFalse(actualOrderJson.contains("\"paid\":true"));
    }

    @Test
    public void shouldGetAllResolvedFromDB() {
        List<Order> orders = orderService.findAllByResolvedIsTrue();

        //checking if orders list contain only resolved orders
        assertTrue(orders.stream().allMatch(Order::isResolved));

        //data-h2.sql file contain only 2 order inserts that are resolved
        assertEquals(2, orders.size());
    }

    @Test
    public void shouldGetAllResolvedFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/resolved")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertFalse(actualOrderJson.contains("\"resolved\":false"));
    }

    @Test
    public void shouldGetFinalizedDineInByIdFromDB() {
        Order order = orderService.findFinalizedById(3, false).orElse(new Order());

        assertTrue(order.isPaid());
        assertTrue(order.isResolved());
        assertFalse(order.isForTakeAway());
    }

    @Test
    public void shouldGetFinalizedDineInByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/finalized/id/3/false")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertFalse(actualOrderJson.contains("\"resolved\":false"));
        assertFalse(actualOrderJson.contains("\"paid\":false"));
        assertFalse(actualOrderJson.contains("\"forTakeAway\":true"));
    }

    @Test
    public void shouldGetAllForTakeAwayFromDB() {
        List<Order> orders = orderService.findAllTakeAway();
        assertEquals(1, orders.size());
    }

    @Test
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
    public void shouldGetByTableNumberFromDB() {
        Order order = orderService.findByTableNumber(2).orElse(new Order());
        //only table number 2 has cash payment method
        assertEquals("cash", order.getPaymentMethod());
    }

    @Test
    public void shouldGetByTableNumberFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/2")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertTrue(actualOrderJson.contains("\"paymentMethod\":\"cash\""));
    }

    @Test
    public void shouldGetByIdFromDB() {
        Order order = orderService.findById(4).orElse(new Order());
        //only order with ID 4 has total amount = 73.50
        assertEquals("73.50", order.getTotalAmount().setScale(2, RoundingMode.HALF_UP).toString());
    }

    @Test
    public void shouldGetByIdFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders/id/4")).andReturn();
        String actualOrderJson = result.getResponse().getContentAsString();
        assertTrue(actualOrderJson.contains("\"totalAmount\":73.50"));
    }
}