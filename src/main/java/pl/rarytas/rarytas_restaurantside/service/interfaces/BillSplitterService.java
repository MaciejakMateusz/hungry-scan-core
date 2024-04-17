package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.BillSplitter;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

public interface BillSplitterService {

    void splitBill(BillSplitter billSplitter);

    BillSplitter findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;
}
