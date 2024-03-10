package pl.rarytas.rarytas_restaurantside.service.archive.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface HistoryOrderService {

    Page<HistoryOrder> findAllForTakeAway(Pageable pageable);

    Page<HistoryOrder> findAllDineIn(Pageable pageable);

    HistoryOrder findById(Long id) throws LocalizedException;

    Long countAll();

    List<HistoryOrder> findFinalizedByDate(String date, boolean forTakeAway);

    void save(HistoryOrder historyOrder);
}
