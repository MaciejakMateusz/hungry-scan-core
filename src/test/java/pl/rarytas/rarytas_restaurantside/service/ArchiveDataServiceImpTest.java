package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryWaiterCallService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.ArchiveDataService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ArchiveDataServiceImpTest {

    @Autowired
    private ArchiveDataService archiveDataService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WaiterCallService waiterCallService;

    @Autowired
    private HistoryOrderService historyOrderService;

    @Autowired
    private HistoryWaiterCallService historyWaiterCallService;

//    @Test
//    @Transactional
//    @Rollback
//    void shouldArchiveOrder() throws LocalizedException {
//        Order existingOrder = orderService.findById(5L);
//        existingOrder.setPaid(true);
//        existingOrder.setPaymentMethod(PaymentMethod.CARD);
//        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
//        assertFalse(waiterCalls.isEmpty());
//        assertEquals("2024-01-29T08:41:20.738823", waiterCalls.get(0).getCallTime().toString());
//
//        archiveDataService.archiveOrder(existingOrder);
//
//        HistoryOrder historyOrder = historyOrderService.findById(5L);
//        assertEquals(PaymentMethod.CARD, historyOrder.getPaymentMethod());
//
//        List<HistoryWaiterCall> historyWaiterCalls = historyWaiterCallService.findAllByHistoryOrder(historyOrder);
//        assertEquals("2024-01-29T08:41:20.738823", historyWaiterCalls.get(0).getCallTime().toString());
//    }

    @Test
    void shouldNotArchiveNotPaid() throws LocalizedException {
        Order existingOrder = orderService.findById(1L);
        assertThrows(LocalizedException.class, () -> archiveDataService.archiveOrder(existingOrder));
    }
}