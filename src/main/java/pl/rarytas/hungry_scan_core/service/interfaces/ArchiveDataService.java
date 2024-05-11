package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.Order;
import pl.rarytas.hungry_scan_core.entity.OrderSummary;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

public interface ArchiveDataService {

    void archiveSummary(OrderSummary orderSummary);

    void archiveOrder(Order order) throws LocalizedException;

}
