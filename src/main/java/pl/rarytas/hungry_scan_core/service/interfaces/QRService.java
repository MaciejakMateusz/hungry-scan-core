package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.RestaurantTable;

import java.io.File;

public interface QRService {
    File generate(RestaurantTable table) throws Exception;
}
