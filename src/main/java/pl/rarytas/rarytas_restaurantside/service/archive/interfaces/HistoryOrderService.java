package pl.rarytas.rarytas_restaurantside.service.archive.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;

import java.util.List;
import java.util.Optional;

public interface HistoryOrderService {
    List<HistoryOrder> findAllNotPaid();

    List<HistoryOrder> findAllByResolvedIsTrue();

    Page<HistoryOrder> findAllFinalized(boolean forTakeAway, Pageable pageable);

    Optional<HistoryOrder> findFinalizedById(Long id, boolean forTakeAway);

    List<HistoryOrder> findFinalizedByDate(String date, boolean forTakeAway);

    List<HistoryOrder> findAllResolvedTakeAwayLimit50();

    Optional<HistoryOrder> findById(Long id);

    void save(HistoryOrder historyOrder);

    boolean existsByIdAndForTakeAwayAndResolved(Long id, boolean forTakeAway);
}
