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
}
