package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.repository.OrderSummaryRepository;
import com.hackybear.hungry_scan_core.service.interfaces.ArchiveDataService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProcessor {

    private final RestaurantTableService restaurantTableService;
    private final OrderSummaryRepository summaryRepository;
    private final ArchiveDataService archiveDataService;

    public OrderSummary pay(OrderSummary orderSummary) {
        OrderSummary summary;
        return switch (orderSummary.getPaymentMethod()) {
            case CASH, CARD:
                summary = summaryRepository.saveAndFlush(orderSummary);
                convertAndSendMessages();
                yield summary;
            case BLIK:
                summary = handleBlikPayment(orderSummary);
                convertAndSendMessages();
                yield summary;
            case APPLE:
                summary = handleApplePayment(orderSummary);
                convertAndSendMessages();
                yield summary;
        };
    }

    private OrderSummary handleBlikPayment(OrderSummary orderSummary) {
        //TODO sztuczna implementacja metody obsługującej BLIK - zamienić w przyszłości na prawdziwą
        orderSummary.setPaid(true);
        summaryRepository.saveAndFlush(orderSummary);
        archiveDataService.archiveSummary(orderSummary);
        return new OrderSummary();
    }

    private OrderSummary handleApplePayment(OrderSummary orderSummary) {
        //TODO sztuczna implementacja metody obsługującej ApplePay - zamienić w przyszłości na prawdziwą
        orderSummary.setPaid(true);
        OrderSummary summary = summaryRepository.save(orderSummary);
        archiveDataService.archiveSummary(summary);
        return new OrderSummary();
    }

    private void convertAndSendMessages() {
        List<RestaurantTable> allTables = restaurantTableService.findAll();
        //todo websocket msg
        List<OrderSummary> allSummaries = summaryRepository.findAll();
        //todo websocket msg
    }
}
