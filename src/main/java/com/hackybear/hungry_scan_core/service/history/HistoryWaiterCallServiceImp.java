package com.hackybear.hungry_scan_core.service.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryOrder;
import com.hackybear.hungry_scan_core.entity.history.HistoryWaiterCall;
import com.hackybear.hungry_scan_core.repository.history.HistoryWaiterCallRepository;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryWaiterCallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
