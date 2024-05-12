package com.hackybear.hungry_scan_core.controller.restaurant;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.entity.OrderedItem;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import com.hackybear.hungry_scan_core.test_utils.OrderFactory;
import com.hackybear.hungry_scan_core.test_utils.OrderedItemFactory;
import com.hackybear.hungry_scan_core.utility.Money;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderedItemControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private OrderFactory orderFactory;

    @Autowired
    private OrderedItemFactory orderedItemFactory;

    private Order table9Order = new Order();
    private Order table11Order = new Order();

    @Sql("/data-h2.sql")
    @Test
    @org.junit.jupiter.api.Order(1)
    @WithMockUser(roles = {"CUSTOMER"})
    void init() throws Exception {
        log.info("Initializing H2 database...");
        log.info("Database initialized.");
        log.info("Preparing orders for tests...");
        this.table9Order = orderFactory.createOrder(9, false,
                orderedItemFactory.createOrderedItem(31, null, "Extra warm please.", 2),
                orderedItemFactory.createOrderedItem(21, 4, null, 1),
                orderedItemFactory.createOrderedItem(32, null, null, 1));
        this.table11Order = orderFactory.createOrder(11, false,
                orderedItemFactory.createOrderedItem(22, 6, "no salt please", 1),
                orderedItemFactory.createOrderedItem(32, null, null, 1),
                orderedItemFactory.createOrderedItem(33, null, "with ice pls", 1));
        this.table9Order.getRestaurantTable().setActive(true);
        apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table9Order, OrderSummary.class);
        this.table11Order.getRestaurantTable().setActive(true);
        apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table11Order, OrderSummary.class);
        log.info("Orders prepared.");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void orderInitializationCheck() {
        assertNotNull(table9Order);
        assertEquals(Money.of(56.00), table9Order.getTotalAmount());
        assertNotNull(table11Order);
        assertEquals(Money.of(42.50), table11Order.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldFindAll() throws Exception {
        List<OrderedItem> items = apiRequestUtils.fetchAsList("/api/restaurant/ordered-items", OrderedItem.class);
        assertEquals(6, items.size());
    }

    @Test
    void shouldNotAllowUnauthorizedToFindAll() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/restaurant/ordered-items");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldReturnForbiddenStatusOnFindAll() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/restaurant/ordered-items");
    }

    @Test
    @WithMockUser(roles = {"WAITER"})
    void shouldFindAllDrinks() throws Exception {
        List<OrderedItem> drinks = apiRequestUtils.fetchAsList("/api/restaurant/ordered-items/drinks", OrderedItem.class);
        assertEquals(4, drinks.size());
    }

    @Test
    void shouldNotAllowUnauthorizedToFindAllDrinks() throws Exception {
        apiRequestUtils.fetchAndExpectUnauthorized("/api/restaurant/ordered-items/drinks");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldReturnForbiddenStatusOnFindAllDrinks() throws Exception {
        apiRequestUtils.fetchAndExpectForbidden("/api/restaurant/ordered-items/drinks");
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldShow() throws Exception {
        OrderedItem item =
                apiRequestUtils.postAndFetchObject("/api/restaurant/ordered-items/show", 5, OrderedItem.class);
        assertEquals(5, item.getId());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotShowWhenNonExisting() throws Exception {
        Map<?, ?> errors =
                apiRequestUtils.postAndExpectErrors("/api/restaurant/ordered-items/show", 456);
        assertEquals("Zamówiona pozycja z podanym ID = 456 nie istnieje.", errors.get("exceptionMsg"));
    }

    @Test
    void shouldNotAllowUnauthorizedToShow() throws Exception {
        apiRequestUtils.postAndExpectUnauthorized("/api/restaurant/ordered-items/show", 4);
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"})
    void shouldReturnForbiddenStatusOnShow() throws Exception {
        apiRequestUtils.postAndExpect("/api/restaurant/ordered-items/show", 4, status().isForbidden());
    }


}
