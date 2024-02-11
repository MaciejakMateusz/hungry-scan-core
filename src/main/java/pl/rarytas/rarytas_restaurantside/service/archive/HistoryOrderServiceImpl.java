package pl.rarytas.rarytas_restaurantside.service.archive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.repository.archive.HistoryOrderRepository;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HistoryOrderServiceImpl implements HistoryOrderService {

    private final HistoryOrderRepository historyOrderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public HistoryOrderServiceImpl(HistoryOrderRepository historyOrderRepository, SimpMessagingTemplate messagingTemplate) {
        this.historyOrderRepository = historyOrderRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public List<HistoryOrder> findAllNotPaid() {
        return historyOrderRepository.findAllNotPaid();
    }

    @Override
    public List<HistoryOrder> findAllByResolvedIsTrue() {
        return historyOrderRepository.findAllResolved();
    }

    @Override
    public Long countResolved() {
        return historyOrderRepository.count();
    }
    @Override
    public Page<HistoryOrder> findAllFinalized(boolean isForTakeAway,
                                               Pageable pageable) {
        return historyOrderRepository.findAllByForTakeAway(isForTakeAway, pageable);
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
    public Optional<HistoryOrder> findById(Long id) {
        return historyOrderRepository.findById(id);
    }

    @Override
    public void save(HistoryOrder historyOrder) {
        if (orderExistsForGivenTable(historyOrder)) {
            return;
        }
        historyOrderRepository.save(historyOrder);
        if (!historyOrder.isForTakeAway()) {
            messagingTemplate.convertAndSend("/topic/restaurant-orders", findAllNotPaid());
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
    public boolean existsByIdAndForTakeAwayAndResolved(Long id, boolean forTakeAway) {
        return historyOrderRepository.existsByIdForTakeWayIsResolved(id, forTakeAway, true);
    }
}