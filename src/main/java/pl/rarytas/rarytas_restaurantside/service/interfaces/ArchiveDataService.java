package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderSummary;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

public interface ArchiveDataService {

    void archiveSummary(OrderSummary orderSummary);

    void archiveOrder(Order order) throws LocalizedException;

}
