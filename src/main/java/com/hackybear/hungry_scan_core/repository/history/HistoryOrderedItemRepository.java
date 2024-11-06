package com.hackybear.hungry_scan_core.repository.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryOrderedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryOrderedItemRepository extends JpaRepository<HistoryOrderedItem, Long> {
}