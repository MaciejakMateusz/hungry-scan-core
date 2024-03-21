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
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryWaiterCall;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderService;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryWaiterCallService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.ArchiveDataService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    @Test
    @Transactional
    void shouldArchiveOrder() throws LocalizedException {
        Order existingOrder = orderService.findById(4L);
        List<WaiterCall> waiterCalls = waiterCallService.findAllByOrder(existingOrder);
        assertFalse(waiterCalls.isEmpty());
        assertEquals( "2024-01-29T08:41:20.738823", waiterCalls.get(0).getCallTime().toString());

        archiveDataService.archiveOrder(existingOrder);

        HistoryOrder historyOrder = historyOrderService.findById(4L);
        assertEquals("online", historyOrder.getPaymentMethod());

        List<HistoryWaiterCall> historyWaiterCalls = historyWaiterCallService.findAllByHistoryOrder(historyOrder);
        assertEquals( "2024-01-29T08:41:20.738823", historyWaiterCalls.get(0).getCallTime().toString());
    }

    @Test
    void shouldNotArchiveNotPaid() throws LocalizedException {
        Order existingOrder = orderService.findById(1L);
        assertThrows(LocalizedException.class, () -> archiveDataService.archiveOrder(existingOrder));
    }
}