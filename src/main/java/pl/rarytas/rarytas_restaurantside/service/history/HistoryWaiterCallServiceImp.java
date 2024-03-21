package pl.rarytas.rarytas_restaurantside.service.history;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryWaiterCall;
import pl.rarytas.rarytas_restaurantside.repository.history.HistoryWaiterCallRepository;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryWaiterCallService;

import java.util.List;

@Slf4j
@Service
public class HistoryWaiterCallServiceImp implements HistoryWaiterCallService {

    private final HistoryWaiterCallRepository historyWaiterCallRepository;

    public HistoryWaiterCallServiceImp(HistoryWaiterCallRepository historyWaiterCallRepository) {
        this.historyWaiterCallRepository = historyWaiterCallRepository;
    }

    @Override
    public void save(HistoryWaiterCall historyWaiterCall) {
        historyWaiterCallRepository.save(historyWaiterCall);
    }

    @Override
    public List<HistoryWaiterCall> findAllByHistoryOrder(HistoryOrder historyOrder) {
        return historyWaiterCallRepository.findAllByHistoryOrder(historyOrder);
    }
}
