package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.entity.history.HistoryOrder;
import com.hackybear.hungry_scan_core.entity.history.HistoryOrderSummary;
import com.hackybear.hungry_scan_core.entity.history.HistoryOrderedItem;
import com.hackybear.hungry_scan_core.entity.history.HistoryWaiterCall;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import com.hackybear.hungry_scan_core.repository.OrderSummaryRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantTableRepository;
import com.hackybear.hungry_scan_core.repository.WaiterCallRepository;
import com.hackybear.hungry_scan_core.repository.history.HistoryWaiterCallRepository;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryOrderSummaryService;
import com.hackybear.hungry_scan_core.utility.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveDataServiceImpTest {

    @Mock
    private HistoryOrderSummaryService historyOrderSummaryService;
    @Mock
    private OrderSummaryRepository orderSummaryRepository;
    @Mock
    private WaiterCallRepository waiterCallRepository;
    @Mock
    private HistoryWaiterCallRepository historyWaiterCallRepository;
    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @InjectMocks
    private ArchiveDataServiceImp service;

    private Restaurant restaurant;
    private RestaurantTable table;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Chez Test");

        table = new RestaurantTable();
        table.setId(2L);
        table.setNumber(5);
    }

    @Test
    void testArchiveSummaryWithOrders() {
        OrderedItem oi = new OrderedItem();
        oi.setId(10L);
        MenuItem menuItem = new MenuItem();
        menuItem.setId(45L);
        menuItem.setName(new Translatable().withPl("Burger"));
        menuItem.setPrice(Money.of(8.50));
        oi.setMenuItem(menuItem);
        Variant variant = new Variant();
        variant.setName(new Translatable().withPl("Large"));
        oi.setVariant(variant);
        Ingredient ingredient = new Ingredient();
        ingredient.setName(new Translatable().withPl("Cheese"));
        oi.setAdditionalIngredients(Set.of(ingredient));
        oi.setAdditionalComment("No onions");
        oi.setQuantity(2);
        oi.setPaid(true);

        Order order = new Order();
        order.setId(20L);
        order.setRestaurant(restaurant);
        order.setRestaurantTable(table);
        LocalDateTime orderTime = LocalDateTime.of(2025, 5, 24, 13, 15);
        order.setOrderTime(orderTime);
        order.setTotalAmount(BigDecimal.valueOf(17.00));
        order.setResolved(false);
        order.setOrderedItems(List.of(oi));

        OrderSummary summary = new OrderSummary();
        summary.setId(30L);
        summary.setRestaurant(restaurant);
        summary.setRestaurantTable(table);
        summary.setInitialOrderDate(LocalDate.of(2025, 5, 24));
        summary.setInitialOrderTime(LocalTime.of(13, 0));
        summary.setTipAmount(BigDecimal.valueOf(2.00));
        summary.setTotalAmount(BigDecimal.valueOf(19.00));
        summary.setPaid(false);
        summary.setBillSplitRequested(true);
        summary.setPaymentMethod(PaymentMethod.CASH);
        summary.setOrders(List.of(order));

        service.archiveSummary(summary);

        ArgumentCaptor<HistoryOrderSummary> histCap =
                ArgumentCaptor.forClass(HistoryOrderSummary.class);
        verify(historyOrderSummaryService).save(histCap.capture());

        HistoryOrderSummary hs = histCap.getValue();
        assertEquals(30L, hs.getId());
        assertSame(restaurant, hs.getRestaurant());
        assertSame(table, hs.getRestaurantTable());
        assertEquals(LocalDate.of(2025, 5, 24), hs.getInitialOrderDate());
        assertEquals(LocalTime.of(13, 0), hs.getInitialOrderTime());
        assertEquals(BigDecimal.valueOf(2.00), hs.getTipAmount());
        assertEquals(BigDecimal.valueOf(19.00), hs.getTotalAmount());
        assertFalse(hs.isPaid());
        assertTrue(hs.isBillSplitRequested());
        assertEquals(PaymentMethod.CASH, hs.getPaymentMethod());

        List<HistoryOrder> hOrders = hs.getHistoryOrders();
        assertEquals(1, hOrders.size());
        HistoryOrder ho = hOrders.getFirst();
        assertEquals(20L, ho.getId());
        assertSame(restaurant, ho.getRestaurant());
        assertSame(table, ho.getRestaurantTable());
        assertEquals(orderTime.toLocalDate(), ho.getOrderDate());
        assertEquals(orderTime.toLocalTime(), ho.getOrderTime());
        assertEquals(Money.of(17.0), ho.getTotalAmount());
        assertFalse(ho.isResolved());

        List<HistoryOrderedItem> hisItems = ho.getHistoryOrderedItems();
        assertEquals(1, hisItems.size());
        HistoryOrderedItem hi = hisItems.getFirst();
        assertEquals(10L, hi.getId());
        assertEquals("Burger", hi.getMenuItem().getName().getPl());
        assertEquals("Large", hi.getVariant().getName().getPl());
        assertEquals("Cheese", hi.getAdditionalIngredients().iterator().next().getName().getPl());
        assertEquals("No onions", hi.getAdditionalComment());
        assertEquals(2, hi.getQuantity());
        assertTrue(hi.isPaid());

        verify(orderSummaryRepository).delete(summary);
        verifyNoMoreInteractions(historyOrderSummaryService, orderSummaryRepository);
    }

    @Test
    void testArchiveSummaryWithNoOrders() {
        OrderSummary summary = new OrderSummary();
        summary.setId(40L);
        summary.setRestaurant(restaurant);
        summary.setRestaurantTable(table);
        summary.setInitialOrderDate(LocalDate.of(2025, 5, 24));
        summary.setInitialOrderTime(LocalTime.of(14, 0));
        summary.setTipAmount(BigDecimal.ZERO);
        summary.setTotalAmount(BigDecimal.ZERO);
        summary.setPaid(true);
        summary.setBillSplitRequested(false);
        summary.setPaymentMethod(PaymentMethod.CARD);
        summary.setOrders(Collections.emptyList());

        service.archiveSummary(summary);

        ArgumentCaptor<HistoryOrderSummary> histCap =
                ArgumentCaptor.forClass(HistoryOrderSummary.class);
        verify(historyOrderSummaryService).save(histCap.capture());
        assertTrue(histCap.getValue().getHistoryOrders().isEmpty());

        verify(orderSummaryRepository).delete(summary);
        verifyNoMoreInteractions(historyOrderSummaryService, orderSummaryRepository);
    }

    @Test
    void testArchiveWaiterCallWithCalls() {
        WaiterCall c1 = new WaiterCall();
        c1.setId(50L);
        LocalDateTime t1 = LocalDateTime.of(2025, 5, 24, 15, 0);
        c1.setCallTime(t1);
        c1.setResolvedTime(t1.plusMinutes(5));
        c1.setResolved(true);

        WaiterCall c2 = new WaiterCall();
        c2.setId(51L);
        LocalDateTime t2 = LocalDateTime.of(2025, 5, 24, 16, 0);
        c2.setCallTime(t2);
        c2.setResolvedTime(null);
        c2.setResolved(false);

        List<WaiterCall> original = new ArrayList<>(List.of(c1, c2));
        table.setWaiterCalls(original);

        service.archiveWaiterCall(table);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<HistoryWaiterCall>> cap =
                ArgumentCaptor.forClass(List.class);
        verify(historyWaiterCallRepository).saveAll(cap.capture());

        List<HistoryWaiterCall> hist = cap.getValue();
        assertEquals(2, hist.size());
        HistoryWaiterCall h1 = hist.getFirst();
        assertEquals(50L, h1.getId());
        assertEquals(2L, h1.getTableId());
        assertEquals(5, h1.getTableNumber());
        assertEquals(t1, h1.getCallTime());
        assertEquals(t1.plusMinutes(5), h1.getResolvedTime());
        assertTrue(h1.isResolved());

        HistoryWaiterCall h2 = hist.get(1);
        assertEquals(51L, h2.getId());
        assertEquals(2L, h2.getTableId());
        assertEquals(5, h2.getTableNumber());
        assertEquals(t2, h2.getCallTime());
        assertNull(h2.getResolvedTime());
        assertFalse(h2.isResolved());

        verify(waiterCallRepository).deleteAll(original);
        assertTrue(table.getWaiterCalls().isEmpty(),
                "after archive, table.waiterCalls should be cleared");
        verify(restaurantTableRepository).save(table);

        verifyNoMoreInteractions(historyWaiterCallRepository,
                waiterCallRepository,
                restaurantTableRepository);
    }

    @Test
    void testArchiveWaiterCallWithNoCalls() {
        table.setWaiterCalls(Collections.emptyList());

        service.archiveWaiterCall(table);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<HistoryWaiterCall>> cap =
                ArgumentCaptor.forClass(List.class);
        verify(historyWaiterCallRepository).saveAll(cap.capture());
        assertTrue(cap.getValue().isEmpty());

        verify(waiterCallRepository).deleteAll(Collections.emptyList());
        assertTrue(table.getWaiterCalls().isEmpty());
        verify(restaurantTableRepository).save(table);

        verifyNoMoreInteractions(historyWaiterCallRepository,
                waiterCallRepository,
                restaurantTableRepository);
    }

    @Test
    void testArchiveOrderDoesNothing() {
        Order dummy = new Order();
        service.archiveOrder(dummy);

        verifyNoInteractions(historyOrderSummaryService,
                orderSummaryRepository,
                waiterCallRepository,
                historyWaiterCallRepository,
                restaurantTableRepository);
    }
}
