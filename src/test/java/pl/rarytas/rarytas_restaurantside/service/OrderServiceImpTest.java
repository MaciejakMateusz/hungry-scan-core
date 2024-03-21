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
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderService;
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
class OrderServiceImpTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HistoryOrderService historyOrderService;

    @Autowired
    private WaiterCallService waiterCallService;

    @Autowired
    OrderProcessor orderProcessor;

    @Test
    @org.junit.jupiter.api.Order(1)
    void shouldFindAllNotPaid() {
        assertEquals(3, orderService.findAll().size());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void shouldFindAllTakeAway() {
        assertEquals(1, orderService.findAllTakeAway().size());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void shouldFindById() throws LocalizedException {
        Order order = orderService.findById(2L);
        int orderNumber = order.getOrderNumber();
        assertEquals(322, orderNumber);
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> orderService.findById(23L));
    }


    @Test
    @org.junit.jupiter.api.Order(5)
    void shouldFindByTableNumber() throws LocalizedException {
        Order order = orderService.findByTableNumber(5);
        int orderNumber = order.getOrderNumber();
        assertEquals(421, orderNumber);
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void shouldNotFindByTableNumber() {
        assertThrows(LocalizedException.class, () -> orderService.findByTableNumber(15));
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void shouldSave() throws LocalizedException {
        Order order = orderProcessor.createDineInOrder(16, List.of(4, 15, 25));
        orderService.save(order);

        Order persistedOrder = orderService.findById(5L);

        assertEquals(3, persistedOrder.getOrderedItems().size());
        assertEquals(16, persistedOrder.getRestaurantTable().getId());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void shouldNotSaveForTheSameTable() throws LocalizedException {
        Order order = orderProcessor.createDineInOrder(16, List.of(1, 2, 33));
        assertThrows(LocalizedException.class, () -> orderService.save(order));
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void shouldSaveTakeAway() throws LocalizedException {
        Order order = orderProcessor.createTakeAwayOrder(List.of(4, 15, 25));
        order.setPaymentMethod("online");
        order.setPaid(true);
        orderService.saveTakeAway(order);

        Order persistedOrder = orderService.findById(6L);

        assertEquals(3, persistedOrder.getOrderedItems().size());
        assertEquals(19, persistedOrder.getRestaurantTable().getId());
    }

    //TODO czy naprawdę nie ma przypadku, gdzie nie pozwolimy na zapisanie zamówienia na wynos?

    @Test
    @org.junit.jupiter.api.Order(10)
    void shouldOrderMoreDishes() throws LocalizedException {
        Order order = orderProcessor.createDineInOrder(16, List.of(12, 13, 14));
        order.setId(5L);

        orderService.orderMoreDishes(order);

        Order persistedOrder = orderService.findById(5L);

        assertEquals(6, persistedOrder.getOrderedItems().size());
        assertEquals(16, persistedOrder.getRestaurantTable().getId());
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void shouldNotOrderMoreDishes() throws LocalizedException {
        Order order = orderProcessor.createDineInOrder(16, List.of(12, 13, 14));
        order.setId(666L);

        assertThrows(LocalizedException.class, () -> orderService.orderMoreDishes(order));
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    void shouldRequestBill() throws LocalizedException {
        Order order = orderService.findById(1L);
        assertFalse(order.isBillRequested());

        order.setPaymentMethod(PaymentMethod.CARD.name());
        orderService.requestBill(order.getId(), order.getPaymentMethod());

        order = orderService.findById(1L);
        assertTrue(order.isBillRequested());
    }

    @Test
    @org.junit.jupiter.api.Order(13)
    void shouldNotRequestBillWithActiveRequest() {
        assertThrows(LocalizedException.class, () -> orderService.requestBill(1L, PaymentMethod.CASH.name()));
    }

    @Test
    @org.junit.jupiter.api.Order(14)
    void shouldNotRequestBillWithActiveWaiterCall() {
        assertThrows(LocalizedException.class, () -> orderService.requestBill(3L, PaymentMethod.CARD.name()));
    }

    @Test
    @org.junit.jupiter.api.Order(15)
    void shouldFinishAndArchive() throws LocalizedException {
        orderService.finish(2L);

        HistoryOrder historyOrder = historyOrderService.findById(2L);
        assertNotNull(historyOrder);
        assertEquals(HistoryOrder.class, historyOrder.getClass());
        assertEquals("cash", historyOrder.getPaymentMethod());

        assertThrows(LocalizedException.class, () -> orderService.findById(2L));
    }

    @Test
    @org.junit.jupiter.api.Order(16)
    void shouldNotFinishAndArchive() {
        assertThrows(LocalizedException.class, () -> orderService.finish(15L));
    }

    @Test
    @org.junit.jupiter.api.Order(17)
    void shouldFinishAndArchiveTakeAway() throws LocalizedException {
        orderService.finishTakeAway(6L);

        HistoryOrder historyOrder = historyOrderService.findById(6L);
        assertEquals(HistoryOrder.class, historyOrder.getClass());
        assertEquals("online", historyOrder.getPaymentMethod());
        assertTrue(historyOrder.isPaid());

        assertThrows(LocalizedException.class, () -> orderService.findById(6L));
    }

    @Test
    @org.junit.jupiter.api.Order(18)
    void shouldNotFinishAndArchiveTakeAway() {
        assertThrows(LocalizedException.class, () -> orderService.finishTakeAway(21L));
    }

    @Test
    @org.junit.jupiter.api.Order(19)
    void shouldCallWaiter() throws LocalizedException {
        Order existingOrder = orderService.findById(5L);
        assertNotNull(existingOrder);
        assertEquals(6, existingOrder.getOrderedItems().size());

        orderService.callWaiter(5L);
        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        waiterCalls.forEach(waiterCall -> assertEquals(waiterCall.getOrder().toString(), existingOrder.toString()));
    }

    @Test
    @org.junit.jupiter.api.Order(20)
    void shouldNotCallWaiterWithActiveWaiterCall() {
        assertThrows(LocalizedException.class, () -> orderService.callWaiter(5L));
    }

    @Test
    @org.junit.jupiter.api.Order(21)
    void shouldNotCallWaiterWithActiveBillRequest() throws LocalizedException {
        Order order = orderProcessor.createDineInOrder(10, List.of(15, 17));
        order.setBillRequested(true);
        orderService.save(order); //persisted order's ID = 7
        assertThrows(LocalizedException.class, () -> orderService.callWaiter(7L));
    }

    @Test
    @org.junit.jupiter.api.Order(22)
    void shouldResolveWaiterCall() throws LocalizedException {
        orderService.resolveWaiterCall(5L);
        Order existingOrder = orderService.findById(1L);

        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        waiterCalls.forEach(waiterCall -> assertTrue(waiterCall.isResolved()));
    }

    @Test
    @org.junit.jupiter.api.Order(23)
    void shouldNotResolveWaiterCall() {
        assertThrows(LocalizedException.class, () -> orderService.resolveWaiterCall(5L));
    }

    @Test
    @org.junit.jupiter.api.Order(24)
    void shouldDelete() throws LocalizedException {
        Order existingOrder = orderService.findById(1L);
        assertEquals("2024-01-29T08:29:20.738823", existingOrder.getOrderTime().toString());
        orderService.delete(existingOrder);
        assertThrows(LocalizedException.class, () -> orderService.findById(1L));
    }

    //TODO kiedy nie pozwalać na usuwanie zamówienia? Funkcjonalność jest tylko dla obsługi restauracji

}