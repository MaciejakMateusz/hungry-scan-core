package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Order;

public interface ArchiveDataService {
    void archiveOrder(Order order);
}