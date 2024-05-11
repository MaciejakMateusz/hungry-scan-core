package com.hackybear.hungry_scan_core.service.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryOrderedItem;
import com.hackybear.hungry_scan_core.repository.history.HistoryOrderedItemRepository;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryOrderedItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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