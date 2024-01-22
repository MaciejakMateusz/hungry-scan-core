package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;

import java.util.List;
import java.util.Optional;

public interface HistoryOrderServiceInterface {
    List<HistoryOrder> findAllNotPaid();

    List<HistoryOrder> findAllTakeAway();

    List<HistoryOrder> findAllByResolvedIsTrue();

    List<HistoryOrder> findAllFinalized(boolean forTakeAway, Integer limit, Integer offset);

    Optional<HistoryOrder> findFinalizedById(Long id, boolean forTakeAway);

    List<HistoryOrder> findFinalizedByDate(String date, boolean forTakeAway);

    List<HistoryOrder> findAllResolvedTakeAwayLimit50();

    Optional<HistoryOrder> findByTableNumber(Integer number);

    Optional<HistoryOrder> findById(Long id);

    void save(HistoryOrder historyOrder);

    void saveTakeAway(HistoryOrder historyOrder);

    boolean existsByIdAndForTakeAwayAndResolved(Long id, boolean forTakeAway);
}
