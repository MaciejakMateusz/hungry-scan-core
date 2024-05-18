package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.WaiterCall;
import com.hackybear.hungry_scan_core.repository.WaiterCallRepository;
import com.hackybear.hungry_scan_core.service.interfaces.WaiterCallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WaiterCallServiceImp implements WaiterCallService {

    private final WaiterCallRepository waiterCallRepository;

    public WaiterCallServiceImp(WaiterCallRepository waiterCallRepository) {
        this.waiterCallRepository = waiterCallRepository;
    }

    @Override
    public void save(WaiterCall waiterCall) {
        waiterCallRepository.save(waiterCall);
    }


    @Override
    public void delete(WaiterCall waiterCall) {
        waiterCallRepository.delete(waiterCall);
    }
}
