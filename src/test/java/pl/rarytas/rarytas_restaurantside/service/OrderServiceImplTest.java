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
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;
import pl.rarytas.rarytas_restaurantside.testSupport.OrderProcessor;

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
    private OrderService orderService;

    @Autowired
    private WaiterCallService waiterCallService;

    @Autowired
    OrderProcessor orderProcessor;

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
        Order order = orderService.findFinalizedById(3L, false).orElse(new Order());
        assertEquals(BigDecimal.valueOf(44).setScale(2, RoundingMode.HALF_UP), order.getTotalAmount());
    }

    @Test
    void shouldNotFindFinalizedById() {
        assertThrows(NoSuchElementException.class, () -> orderService.findFinalizedById(12L, false).orElseThrow());
        assertThrows(NoSuchElementException.class, () -> orderService.findFinalizedById(3L, true).orElseThrow());
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
        Order order = (Order) orderService.findById(2L).orElseThrow();
        int orderNumber = order.getOrderNumber();
        assertEquals(322, orderNumber);
    }

    @Test
    void shouldNotFindById() {
        assertThrows(NoSuchElementException.class, () -> orderService.findById(23L).orElseThrow());
    }

    @Test
    @Transactional
    void shouldRequestBill() throws LocalizedException {
        Order activeOrder = (Order) orderService.findById(1L).orElseThrow();
        Integer tableNumber = activeOrder.getRestaurantTable().getId();
        assertEquals(1, tableNumber);

        activeOrder.setPaymentMethod(String.valueOf(PaymentMethod.CARD));
        orderService.requestBill(activeOrder);

        activeOrder = (Order) orderService.findById(1L).orElseThrow();
        assertTrue(activeOrder.isBillRequested(), "The value should be true, but was false");
    }

    @Test
    @Transactional
    void shouldFinishAndArchive() throws LocalizedException {
        Order existingOrder = (Order) orderService.findById(2L).orElseThrow();
        assertTrue(existingOrder.isBillRequested());

        orderService.finish(2L, true, true);
        assertEquals(HistoryOrder.class, orderService.findById(2L).orElseThrow().getClass());
    }

    @Test
    void shouldNotFinishAndArchive() {
        assertThrows(LocalizedException.class, () -> orderService.finish(15L, true, true));
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void shouldCallWaiter() throws LocalizedException {
        Order existingOrder = (Order) orderService.findById(1L).orElseThrow();
        assertEquals("2024-01-29 08:29:20.738823", existingOrder.getOrderTime());

        orderService.callWaiter(existingOrder);
        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        waiterCalls.forEach(waiterCall -> assertEquals(waiterCall.getOrder().toString(), existingOrder.toString()));
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void shouldResolveWaiterCall() throws LocalizedException {
        orderService.resolveWaiterCall(1L);
        Order existingOrder = (Order) orderService.findById(1L).orElseThrow();

        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        waiterCalls.forEach(waiterCall -> assertTrue(waiterCall.isResolved()));
    }

    @Test
    @Transactional
    void shouldDelete() {
        Order existingOrder = (Order) orderService.findById(1L).orElseThrow();
        assertEquals("2024-01-29 08:29:20.738823", existingOrder.getOrderTime());
        orderService.delete(existingOrder);
        assertThrows(NoSuchElementException.class, () -> orderService.findById(1L).orElseThrow());
    }
}