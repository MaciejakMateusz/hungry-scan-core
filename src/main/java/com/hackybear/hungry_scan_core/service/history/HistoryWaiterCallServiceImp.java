package com.hackybear.hungry_scan_core.service.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryWaiterCall;
import com.hackybear.hungry_scan_core.repository.history.HistoryWaiterCallRepository;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryWaiterCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryWaiterCallServiceImp implements HistoryWaiterCallService {

    private final HistoryWaiterCallRepository historyWaiterCallRepository;

    @Override
    public void save(HistoryWaiterCall historyWaiterCall) {
        historyWaiterCallRepository.save(historyWaiterCall);
    }

}
