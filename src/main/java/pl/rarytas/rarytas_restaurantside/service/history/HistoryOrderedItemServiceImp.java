package pl.rarytas.rarytas_restaurantside.service.history;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrderedItem;
import pl.rarytas.rarytas_restaurantside.repository.history.HistoryOrderedItemRepository;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderedItemService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HistoryOrderedItemServiceImp implements HistoryOrderedItemService {
    private final HistoryOrderedItemRepository historyOrderedItemRepository;

    public HistoryOrderedItemServiceImp(HistoryOrderedItemRepository historyOrderedItemRepository) {
        this.historyOrderedItemRepository = historyOrderedItemRepository;
    }

    @Override
    public List<HistoryOrderedItem> findAll() {
        return historyOrderedItemRepository.findAll();
    }

    @Override
    public Optional<HistoryOrderedItem> findById(Long id) {
        return historyOrderedItemRepository.findById(id);
    }
}