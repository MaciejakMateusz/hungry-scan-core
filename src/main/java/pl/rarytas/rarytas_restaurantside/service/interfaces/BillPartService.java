package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.BillPart;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

public interface BillPartService {

    void save(BillPart billPart);

    BillPart findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;
}
