package com.hackybear.hungry_scan_core.controller.restaurant;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderSummaryControllerTest {

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Autowired
    private OrderFactory orderFactory;

    @Autowired
    private OrderedItemFactory orderedItemFactory;

    private OrderSummary table9Summary = new OrderSummary();
    private OrderSummary table11Summary = new OrderSummary();

    @Sql("/data-h2.sql")
    @Test
    @org.junit.jupiter.api.Order(1)
    void init() {
        log.info("Initializing H2 database...");
        log.info("Database initialized.");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @Transactional
    @WithMockUser(roles = "CUSTOMER")
    void orderInitializationCheck() throws Exception {
        prepareSummaries();
        assertNotNull(table9Summary);
        assertEquals(Money.of(56.00), table9Summary.getTotalAmount());
        assertNotNull(table11Summary);
        assertEquals(Money.of(42.50), table11Summary.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @Transactional
    @Rollback
    void shouldPayByCash() throws Exception {
        prepareSummaries();
        RestaurantTable table = table9Summary.getRestaurantTable();
        assertFalse(table.isBillRequested());

        table.setActive(true);
        table9Summary.setTipAmount(Money.of(50.00));
        table9Summary.setPaymentMethod(PaymentMethod.CASH);
        OrderSummary summary = apiRequestUtils.postAndFetchObject("/api/restaurant/summaries/pay", table9Summary, OrderSummary.class);

        assertTrue(summary.getRestaurantTable().isBillRequested());
        assertEquals(Money.of(50.00), summary.getTipAmount());
        assertEquals(Money.of(106.00), summary.getTotalAmount());
        assertEquals(PaymentMethod.CASH, summary.getPaymentMethod());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @Transactional
    @Rollback
    void shouldPayByCard() throws Exception {
        prepareSummaries();
        RestaurantTable table = table11Summary.getRestaurantTable();
        assertFalse(table.isBillRequested());

        table.setActive(true);
        table11Summary.setPaymentMethod(PaymentMethod.CARD);
        OrderSummary summary = apiRequestUtils.postAndFetchObject("/api/restaurant/summaries/pay", table11Summary, OrderSummary.class);

        assertTrue(summary.getRestaurantTable().isBillRequested());
        assertEquals(Money.of(0.00), summary.getTipAmount());
        assertEquals(Money.of(42.50), summary.getTotalAmount());
        assertEquals(PaymentMethod.CARD, summary.getPaymentMethod());
    }

    @Transactional
    void prepareSummaries() throws Exception {
        log.info("Preparing summaries for tests...");
        Order table9Order = orderFactory.createOrder(9L, false,
                orderedItemFactory.createOrderedItem(31L, null, "Extra warm please.", 2),
                orderedItemFactory.createOrderedItem(21L, 4L, null, 1),
                orderedItemFactory.createOrderedItem(32L, null, null, 1));
        Order table11Order = orderFactory.createOrder(11L, false,
                orderedItemFactory.createOrderedItem(22L, 6L, "no salt please", 1),
                orderedItemFactory.createOrderedItem(32L, null, null, 1),
                orderedItemFactory.createOrderedItem(33L, null, "with ice pls", 1));
        table9Order.getRestaurantTable().setActive(true);
        this.table9Summary =
                apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table9Order, OrderSummary.class);
        table11Order.getRestaurantTable().setActive(true);
        this.table11Summary =
                apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table11Order, OrderSummary.class);
        log.info("Summaries prepared.");
    }
}
