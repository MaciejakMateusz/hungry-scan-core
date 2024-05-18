package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.WaiterCall;

public interface WaiterCallService {
    void save(WaiterCall waiterCall);

    void delete(WaiterCall waiterCall);
}
