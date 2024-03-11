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
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;
import pl.rarytas.rarytas_restaurantside.testSupport.OrderProcessor;

import java.util.List;

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
    private HistoryOrderService historyOrderService;

    @Autowired
    private WaiterCallService waiterCallService;

    @Autowired
    OrderProcessor orderProcessor;

    @Test
    void shouldFindAllNotPaid() {
        assertEquals(3, orderService.findAll().size());
    }

    @Test
    void shouldFindAllTakeAway() {
        assertEquals(1, orderService.findAllTakeAway().size());
    }


    @Test
    void shouldFindByTableNumber() throws LocalizedException {
        Order order = orderService.findByTableNumber(5);
        int orderNumber = order.getOrderNumber();
        assertEquals(421, orderNumber);
    }

    @Test
    void shouldNotFindByTableNumber() {
        assertThrows(LocalizedException.class, () -> orderService.findByTableNumber(15));
    }

    @Test
    void shouldFindById() throws LocalizedException {
        Order order = orderService.findById(2L);
        int orderNumber = order.getOrderNumber();
        assertEquals(322, orderNumber);
    }

    @Test
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> orderService.findById(23L));
    }

    @Test
    @Transactional
    void shouldRequestBill() throws LocalizedException {
        Order activeOrder = orderService.findById(1L);
        Integer tableNumber = activeOrder.getRestaurantTable().getId();
        assertEquals(1, tableNumber);

        activeOrder.setPaymentMethod(String.valueOf(PaymentMethod.CARD));
        orderService.requestBill(activeOrder);

        activeOrder = orderService.findById(1L);
        assertTrue(activeOrder.isBillRequested(), "The value should be true, but was false");
    }

    @Test
    @Transactional
    void shouldFinishAndArchive() throws LocalizedException {
        Order existingOrder = orderService.findById(2L);
        assertTrue(existingOrder.isBillRequested());

        orderService.finish(2L, true, true);
        assertEquals(HistoryOrder.class, historyOrderService.findById(2L).getClass());
    }

    @Test
    void shouldNotFinishAndArchive() {
        assertThrows(LocalizedException.class, () -> orderService.finish(15L, true, true));
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void shouldCallWaiter() throws LocalizedException {
        Order existingOrder = orderService.findById(1L);
        assertEquals("2024-01-29 08:29:20.738823", existingOrder.getOrderTime());

        orderService.callWaiter(existingOrder);
        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        waiterCalls.forEach(waiterCall ->
                assertEquals(waiterCall.getOrder().toString(), existingOrder.toString())
        );
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void shouldResolveWaiterCall() throws LocalizedException {
        orderService.resolveWaiterCall(1L);
        Order existingOrder = orderService.findById(1L);

        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        waiterCalls.forEach(waiterCall -> assertTrue(waiterCall.isResolved()));
    }

    @Test
    @Transactional
    void shouldDelete() throws LocalizedException {
        Order existingOrder = orderService.findById(1L);
        assertEquals("2024-01-29 08:29:20.738823", existingOrder.getOrderTime());
        orderService.delete(existingOrder);
        assertThrows(LocalizedException.class, () -> orderService.findById(1L));
    }
}