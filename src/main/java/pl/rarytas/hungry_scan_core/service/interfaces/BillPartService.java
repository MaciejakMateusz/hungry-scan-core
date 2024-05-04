package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.BillPart;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

public interface BillPartService {

    void save(BillPart billPart);

    BillPart findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;
}
