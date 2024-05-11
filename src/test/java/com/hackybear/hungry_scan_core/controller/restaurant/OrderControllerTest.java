package com.hackybear.hungry_scan_core.controller.restaurant;

import com.hackybear.hungry_scan_core.entity.Order;
import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
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

import java.util.ArrayList;
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
class OrderControllerTest {

    @Autowired
    private OrderFactory orderFactory;

    @Autowired
    private OrderedItemFactory orderedItemFactory;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    private Order table15Order = new Order();
    private Order table12Order = new Order();

    @Sql("/data-h2.sql")
    @Test
    @org.junit.jupiter.api.Order(1)
    void init() throws LocalizedException {
        log.info("Initializing H2 database...");
        log.info("Database initialized.");
        log.info("Creating test Order objects...");
        this.table15Order = orderFactory.createOrder(15, false,
                orderedItemFactory.createOrderedItem(4, 2, "Extra warm please.", 2, "Pomidory", "Mozzarella"),
                orderedItemFactory.createOrderedItem(21, 4, null, 1),
                orderedItemFactory.createOrderedItem(22, 8, "Spicy af", 1, "Cebula", "Kurczak"));
        this.table12Order = orderFactory.createOrder(12, false,
                orderedItemFactory.createOrderedItem(4, 1, null, 1, "Bazylia"),
                orderedItemFactory.createOrderedItem(21, 3, "i want it cold", 3),
                orderedItemFactory.createOrderedItem(22, 6, "no salt please", 1));
        log.info("Order objects created.");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void orderInitializationCheck() {
        assertNotNull(table15Order);
        assertEquals(Money.of(139.00), table15Order.getTotalAmount());
        assertNotNull(table12Order);
        assertEquals(Money.of(129.25), table12Order.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @Transactional
    @Rollback
    void shouldSaveDineInOrder() throws Exception {
        table15Order.getRestaurantTable().setActive(true);
        OrderSummary summary =
                apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table15Order, OrderSummary.class);
        assertEquals(1, summary.getOrders().size());
        assertEquals(3, summary.getOrders().get(0).getOrderedItems().size());
        assertEquals(Money.of(139.00), summary.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @Transactional
    @Rollback
    void shouldSaveMultipleOrders() throws Exception {
        table15Order.getRestaurantTable().setActive(true);
        apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table15Order, OrderSummary.class);
        apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table15Order, OrderSummary.class);
        OrderSummary summary =
                apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table15Order, OrderSummary.class);
        assertEquals(3, summary.getOrders().size());
        assertEquals(Money.of(417.00), summary.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @Transactional
    @Rollback
    void shouldSaveForSeparateTables() throws Exception {
        table15Order.getRestaurantTable().setActive(true);
        table12Order.getRestaurantTable().setActive(true);
        OrderSummary table15Summary = apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table15Order, OrderSummary.class);
        OrderSummary table12Summary = apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table12Order, OrderSummary.class);
        assertEquals(1, table15Summary.getOrders().size());
        assertEquals(Money.of(139.00), table15Summary.getTotalAmount());
        assertEquals(1, table12Summary.getOrders().size());
        assertEquals(Money.of(129.25), table12Summary.getTotalAmount());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotSaveWithNullTable() throws Exception {
        table12Order.setRestaurantTable(null);
        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/restaurant/orders/dine-in", table12Order);
        assertEquals("Podany numer stolika jest niepoprawny.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotSaveWithInactiveTable() throws Exception {
        table15Order.getRestaurantTable().setActive(false);
        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/restaurant/orders/dine-in", table15Order);
        assertEquals("Stolik z ID = 15 nie jest aktywny.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotSaveWithEmptyOrderedItems() throws Exception {
        table15Order.getRestaurantTable().setActive(true);
        table15Order.setOrderedItems(new ArrayList<>());
        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/restaurant/orders/dine-in", table15Order);
        assertEquals("Brak pozycji w zamówieniu.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER_READONLY"})
    void shouldNotAllowUnauthorizedAccessToSaveOrder() throws Exception {
        table15Order.getRestaurantTable().setActive(true);
        apiRequestUtils.postAndExpect("/api/restaurant/orders/dine-in", table15Order, status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @Transactional
    @Rollback
    void shouldGetByTable() throws Exception {
        table15Order.getRestaurantTable().setActive(true);
        apiRequestUtils.postAndFetchObject("/api/restaurant/orders/dine-in", table15Order, OrderSummary.class);

        OrderSummary summary = apiRequestUtils.postAndFetchObject("/api/restaurant/orders/show/table", 15, OrderSummary.class);
        assertEquals(1, summary.getOrders().size());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotGetByTableWhenTableHasNoOrders() throws Exception {
        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/restaurant/orders/show/table", 15);
        assertEquals("Podsumowanie z podanym ID stolika = 15 nie istnieje.", errors.get("exceptionMsg"));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void shouldNotGetByTableWithNonExistingTable() throws Exception {
        Map<?, ?> errors = apiRequestUtils.postAndExpectErrors("/api/restaurant/orders/show/table", 400);
        assertEquals("Podsumowanie z podanym ID stolika = 400 nie istnieje.", errors.get("exceptionMsg"));
    }

}