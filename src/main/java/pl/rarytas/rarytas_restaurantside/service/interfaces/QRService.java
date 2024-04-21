package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.io.File;

public interface QRService {
    File generate(RestaurantTable table) throws Exception;
}
