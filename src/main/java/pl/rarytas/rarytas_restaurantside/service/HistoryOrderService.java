package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.repository.HistoryOrderRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.HistoryOrderServiceInterface;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HistoryOrderService implements HistoryOrderServiceInterface {
    private final HistoryOrderRepository historyOrderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public HistoryOrderService(HistoryOrderRepository historyOrderRepository, SimpMessagingTemplate messagingTemplate) {
        this.historyOrderRepository = historyOrderRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public List<HistoryOrder> findAllNotPaid() {
        return historyOrderRepository.findAllNotPaid();
    }

    @Override
    public List<HistoryOrder> findAllTakeAway() {
        return historyOrderRepository.findAllTakeAway();
    }

    @Override
    public List<HistoryOrder> findAllByResolvedIsTrue() {
        return historyOrderRepository.findAllResolved();
    }

    @Override
    public List<HistoryOrder> findAllFinalized(boolean forTakeAway,
                                               Integer limit,
                                               Integer offset) {
        return historyOrderRepository.findFinalizedOrders(forTakeAway, limit, offset);
    }

    @Override
    public Optional<HistoryOrder> findFinalizedById(Long id, boolean forTakeAway) {
        return historyOrderRepository.findFinalizedById(id, forTakeAway);
    }

    @Override
    public List<HistoryOrder> findFinalizedByDate(String date, boolean forTakeAway) {
        return historyOrderRepository.findFinalizedByDate(date, forTakeAway);
    }

    @Override
    public List<HistoryOrder> findAllResolvedTakeAwayLimit50() {
        return historyOrderRepository.findAllResolvedTakeAwayLimit50();
    }

    @Override
    public Optional<HistoryOrder> findByTableNumber(Integer number) {
        return historyOrderRepository.findNewestOrderByTableNumber(number);
    }

    @Override
    public Optional<HistoryOrder> findById(Long id) {
        return historyOrderRepository.findById(id);
    }

    @Override
    public void save(HistoryOrder historyOrder) {
        if (orderExistsForGivenTable(historyOrder)) {
            return;
        }
        historyOrderRepository.save(historyOrder);
        historyOrderRepository.refresh(historyOrder);
        if (!historyOrder.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-order", findAllNotPaid());
        }
    }

    private boolean orderExistsForGivenTable(HistoryOrder historyOrder) {
        if (historyOrderRepository.existsByRestaurantTable(historyOrder.getRestaurantTable())) {
            HistoryOrder existingHistoryOrder = historyOrderRepository
                    .findNewestOrderByTableNumber(historyOrder.getRestaurantTable().getId())
                    .orElseThrow();
            if (!existingHistoryOrder.isResolved()) {
                log.warn("Order with given table number already exists");
                return true;
            }
        }
        return false;
    }

    @Override
    public void saveTakeAway(HistoryOrder historyOrder) {
        historyOrderRepository.save(historyOrder);
        historyOrderRepository.refresh(historyOrder);
        messagingTemplate.convertAndSend("/topic/takeAway-orders", findAllTakeAway());
    }

    @Override
    public boolean existsByIdAndForTakeAwayAndResolved(Long id, boolean forTakeAway) {
        return historyOrderRepository.existsByIdForTakeWayIsResolved(id, forTakeAway, true);
    }

    private BigDecimal calculateTotalAmount(HistoryOrder historyOrder) {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (OrderedItem orderedItem : historyOrder.getOrderedItems()) {
            BigDecimal itemPrice = orderedItem.getMenuItem().getPrice();
            int quantity = orderedItem.getQuantity();
            sum = sum.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }
        return sum;
    }
}
