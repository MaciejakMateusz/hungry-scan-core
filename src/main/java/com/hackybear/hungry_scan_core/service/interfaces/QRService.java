package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;

public interface QRService {
    void generate(RestaurantTable table) throws Exception;
}
