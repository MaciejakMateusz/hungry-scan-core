package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.RestaurantTable;

public interface QRService {
    void generate() throws Exception;

    void generate(RestaurantTable table, String name) throws Exception;
}
