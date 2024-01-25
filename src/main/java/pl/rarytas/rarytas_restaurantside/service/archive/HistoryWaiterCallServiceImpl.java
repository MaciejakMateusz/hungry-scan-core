package pl.rarytas.rarytas_restaurantside.service.archive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryWaiterCall;
import pl.rarytas.rarytas_restaurantside.repository.archive.HistoryWaiterCallRepository;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryWaiterCallService;

@Slf4j
@Service
public class HistoryWaiterCallServiceImpl implements HistoryWaiterCallService {

    private final HistoryWaiterCallRepository historyWaiterCallRepository;

    public HistoryWaiterCallServiceImpl(HistoryWaiterCallRepository historyWaiterCallRepository) {
        this.historyWaiterCallRepository = historyWaiterCallRepository;
    }

    @Override
    public void save(HistoryWaiterCall historyWaiterCall) {
        historyWaiterCallRepository.save(historyWaiterCall);
    }
}
