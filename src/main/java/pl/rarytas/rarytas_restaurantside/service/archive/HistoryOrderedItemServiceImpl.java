package pl.rarytas.rarytas_restaurantside.service.archive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrderedItem;
import pl.rarytas.rarytas_restaurantside.repository.archive.HistoryOrderedItemRepository;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderedItemService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HistoryOrderedItemServiceImpl implements HistoryOrderedItemService {
    private final HistoryOrderedItemRepository historyOrderedItemRepository;

    public HistoryOrderedItemServiceImpl(HistoryOrderedItemRepository historyOrderedItemRepository) {
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