package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.test_utils.OrderProcessor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderServiceImpTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HistoryOrderService historyOrderService;

    @Autowired
    OrderProcessor orderProcessor;

    @Test
    void shouldFindAllNotPaid() {
        assertEquals(5, orderService.findAll().size());
    }

    @Test
    void shouldFindAllTakeAway() {
        assertEquals(1, orderService.findAllTakeAway().size());
    }

//    @Test
//    void shouldFindById() throws LocalizedException {
//        Order order = orderService.findById(2L);
//        int orderNumber = order.getOrderNumber();
//        assertEquals(322, orderNumber);
//    }

    @Test
    void shouldNotFindById() {
        assertThrows(LocalizedException.class, () -> orderService.findById(23L));
    }


//    @Test
//    void shouldFindByTableNumber() throws LocalizedException {
//        Order order = orderService.findByTableNumber(5);
//        int orderNumber = order.getOrderNumber();
//        assertEquals(421, orderNumber);
//    }

//    @Test
//    void shouldNotFindByTableNumber() {
//        assertThrows(LocalizedException.class, () -> orderService.findByTableNumber(15));
//    }

    @Test
    @Transactional
    @Rollback
    void shouldSave() throws LocalizedException {
        Order order = orderProcessor.createDineInOrder(16, List.of(4, 15, 25));
        orderService.saveDineIn(order);

        Order persistedOrder = orderService.findById(6L);

        assertEquals(3, persistedOrder.getOrderedItems().size());
        assertEquals(16, persistedOrder.getRestaurantTable().getId());
    }

    @Test
    void shouldNotSaveForTheSameTable() throws LocalizedException {
        Order order = orderProcessor.createDineInOrder(1, List.of(1, 2, 33));
        assertThrows(LocalizedException.class, () -> orderService.saveDineIn(order));
    }

//    @Test
//    @Transactional
//    @Rollback
//    void shouldSaveTakeAway() throws LocalizedException {
//        Order order = orderProcessor.createTakeAwayOrder(List.of(4, 15, 25));
//        order.setPaymentMethod(PaymentMethod.ONLINE);
//        order.setPaid(true);
//        orderService.saveTakeAway(order);
//
//        Order persistedOrder = orderService.findById(7L);
//
//        assertEquals(3, persistedOrder.getOrderedItems().size());
//        assertEquals(19, persistedOrder.getRestaurantTable().getId());
//    }

//    @Test
//    @Transactional
//    @Rollback
//    void shouldOrderMoreDishes() throws LocalizedException {
//        Order order = orderProcessor.createDineInOrder(1, List.of(12, 13, 14));
//        order.setId(1L);
//
//        orderService.orderMoreDishes(order);
//
//        Order persistedOrder = orderService.findById(1L);
//
//        assertEquals(4, persistedOrder.getOrderedItems().size());
//        assertEquals(1, persistedOrder.getRestaurantTable().getId());
//    }

//    @Test
//    void shouldNotOrderMoreDishes() throws LocalizedException {
//        Order order = orderProcessor.createDineInOrder(16, List.of(12, 13, 14));
//        order.setId(666L);
//
//        assertThrows(LocalizedException.class, () -> orderService.orderMoreDishes(order));
//    }

//    @Test
//    @Transactional
//    @Rollback
//    void shouldFinishAndArchive() throws LocalizedException {
//        orderService.finish(2L);
//
//        HistoryOrder historyOrder = historyOrderService.findById(2L);
//        assertNotNull(historyOrder);
//        assertEquals(HistoryOrder.class, historyOrder.getClass());
//        assertEquals(PaymentMethod.CASH, historyOrder.getPaymentMethod());
//
//        assertThrows(LocalizedException.class, () -> orderService.findById(2L));
//    }

//    @Test
//    void shouldNotFinishAndArchive() {
//        assertThrows(LocalizedException.class, () -> orderService.finish(15L));
//    }

//    @Test
//    @Transactional
//    @Rollback
//    void shouldFinishAndArchiveTakeAway() throws LocalizedException {
//        orderService.finishTakeAway(4L);
//
//        HistoryOrder historyOrder = historyOrderService.findById(4L);
//        assertEquals(HistoryOrder.class, historyOrder.getClass());
//        assertEquals(PaymentMethod.ONLINE, historyOrder.getPaymentMethod());
//        assertTrue(historyOrder.isPaid());
//
//        assertThrows(LocalizedException.class, () -> orderService.findById(4L));
//    }

    @Test
    void shouldNotFinishAndArchiveTakeAway() {
        assertThrows(LocalizedException.class, () -> orderService.finishTakeAway(21L));
    }

    @Test
    @Transactional
    @Rollback
    void shouldDelete() throws LocalizedException {
        Order existingOrder = orderService.findById(1L);
        assertEquals("2024-01-29T08:29:20.738823", existingOrder.getOrderTime().toString());
        orderService.delete(existingOrder);
        assertThrows(LocalizedException.class, () -> orderService.findById(1L));
    }

    //TODO kiedy nie pozwalać na usuwanie zamówienia? Funkcjonalność jest tylko dla obsługi restauracji

}