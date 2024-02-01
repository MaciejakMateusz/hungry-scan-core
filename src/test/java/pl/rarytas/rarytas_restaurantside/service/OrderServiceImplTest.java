package pl.rarytas.rarytas_restaurantside.service;

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
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;
import pl.rarytas.rarytas_restaurantside.utility.PaymentMethodEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceImplTest {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WaiterCallService waiterCallService;

    @Test
    void shouldFindAllNotPaid() {
        assertEquals(3, orderService.findAllNotPaid().size());
    }

    @Test
    void shouldFindAllTakeAway() {
        assertEquals(1, orderService.findAllTakeAway().size());
    }

    @Test
    void shouldFindAllByResolvedIsTrue() {
        assertEquals(2, orderService.findAllByResolvedIsTrue().size());
    }

    @Test
    void shouldFindFinalizedById() {
        Order order = orderService.findFinalizedById(3, false).orElse(new Order());
        assertEquals(BigDecimal.valueOf(44).setScale(2, RoundingMode.HALF_UP), order.getTotalAmount());
    }

    @Test
    void shouldNotFindFinalizedById() {
        assertThrows(NoSuchElementException.class, () -> orderService.findFinalizedById(12, false).orElseThrow());
        assertThrows(NoSuchElementException.class, () -> orderService.findFinalizedById(3, true).orElseThrow());
    }

    @Test
    void shouldFindByTableNumber() {
        Order order = orderService.findByTableNumber(5).orElse(new Order());
        int orderNumber = order.getOrderNumber();
        assertEquals(421, orderNumber);
    }

    @Test
    void shouldNotFindByTableNumber() {
        assertThrows(NoSuchElementException.class, () -> orderService.findByTableNumber(15).orElseThrow());
    }

    @Test
    void shouldFindById() {
        Order order = orderService.findById(2).orElse(new Order());
        int orderNumber = order.getOrderNumber();
        assertEquals(322, orderNumber);
    }

    @Test
    void shouldNotFindById() {
        assertThrows(NoSuchElementException.class, () -> orderService.findById(23).orElseThrow());
    }

    @Test
    @Transactional
    void shouldSave() throws LocalizedException {
        Order order = orderService.findById(7).orElse(new Order());
        assertNull(order.getOrderNumber(), "Value should be null, but was " + order.getOrderNumber());

        order = createOrder(
                2,
                7);

        orderService.save(order);

        Order foundOrder = orderService.findById(7).orElse(new Order());
        assertEquals(7, foundOrder.getRestaurantTable().getId());
    }

    @Test
    @Transactional
    void shouldNotSaveOrderWithExistingTable() {
        Order order = createOrder(
                2,
                2); //table 2 has already ordered

        assertThrows(LocalizedException.class, () -> orderService.save(order));
    }

    @Test
    @Transactional
    void shouldRequestBill() throws LocalizedException {
        Order activeOrder = orderService.findById(1).orElse(new Order());
        Integer tableNumber = activeOrder.getRestaurantTable().getId();
        assertEquals(1, tableNumber);

        activeOrder.setPaymentMethod(String.valueOf(PaymentMethodEnum.CARD));
        orderService.requestBill(activeOrder);

        activeOrder = orderService.findById(1).orElse(new Order());
        assertTrue(activeOrder.isBillRequested(), "The value should be true, but was false");
    }

    @Test
    @Transactional
    void shouldNotRequestBill() {
        Order order = createOrder(
                1,
                12);
        order.setId(12);
        assertThrows(LocalizedException.class, () -> orderService.requestBill(order));

        order.setId(5); // order with ID=5 has called a waiter already
        assertThrows(LocalizedException.class, () -> orderService.requestBill(order));
    }

    @Test
    @Transactional
    void shouldFinishAndArchive() throws LocalizedException {
        Order existingOrder = orderService.findById(2).orElse(new Order());
        assertTrue(existingOrder.isBillRequested());

        orderService.finish(2, true, true);
        assertThrows(NoSuchElementException.class, () -> orderService.findById(2).orElseThrow());
    }

    @Test
    void shouldNotFinishAndArchive() {
        assertThrows(LocalizedException.class, () -> orderService.finish(15, true, true));
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void shouldCallWaiter() throws LocalizedException {
        Order existingOrder = orderService.findById(1).orElse(new Order());
        assertEquals("2024-01-29 08:29:20.738823", existingOrder.getOrderTime());

        orderService.callWaiter(existingOrder);
        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        waiterCalls.forEach(waiterCall -> assertEquals(waiterCall.getOrder().toString(), existingOrder.toString()));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void shouldResolveWaiterCall() throws LocalizedException {
        orderService.resolveWaiterCall(1);
        Order existingOrder = orderService.findById(1).orElse(new Order());

        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        waiterCalls.forEach(waiterCall -> assertTrue(waiterCall.isResolved()));
    }

    @Test
    @Transactional
    void shouldDelete() {
        Order existingOrder = orderService.findById(1).orElseThrow();
        assertEquals("2024-01-29 08:29:20.738823", existingOrder.getOrderTime());
        orderService.delete(existingOrder);
        assertThrows(NoSuchElementException.class, () -> orderService.findById(1).orElseThrow());
    }

    private Order createOrder(int restaurantId,
                              int tableId) {
        Order order = new Order();
        order.setBillRequested(false);
        order.setForTakeAway(false);
        order.setResolved(false);
        order.setOrderNumber(1);
        order.setOrderTime("2024-01-29 08:29:20.738823");
        order.setPaid(false);
        order.setPaymentMethod(null);
        order.setTotalAmount(null);
        order.setWaiterCalled(false);
        order.setRestaurant(restaurantService.findById(restaurantId).orElseThrow());
        order.setRestaurantTable(restaurantTableService.findById(tableId).orElseThrow());
        return order;
    }
}