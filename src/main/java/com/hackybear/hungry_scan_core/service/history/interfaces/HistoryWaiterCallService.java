package com.hackybear.hungry_scan_core.service.history.interfaces;

import com.hackybear.hungry_scan_core.entity.history.HistoryOrder;
import com.hackybear.hungry_scan_core.entity.history.HistoryWaiterCall;

import java.util.List;

public interface HistoryWaiterCallService {
    void save(HistoryWaiterCall historyWaiterCall);

    List<HistoryWaiterCall> findAllByHistoryOrder(HistoryOrder historyOrder);
}
