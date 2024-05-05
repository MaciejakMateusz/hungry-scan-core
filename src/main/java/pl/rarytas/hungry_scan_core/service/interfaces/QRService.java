package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.RestaurantTable;

public interface QRService {
    void generate(RestaurantTable table) throws Exception;
}
