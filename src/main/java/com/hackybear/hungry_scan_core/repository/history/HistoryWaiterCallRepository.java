package com.hackybear.hungry_scan_core.repository.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryWaiterCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryWaiterCallRepository extends JpaRepository<HistoryWaiterCall, Long> {
}