package pl.rarytas.hungry_scan_core.service.interfaces;

import pl.rarytas.hungry_scan_core.entity.BillSplitter;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;

public interface BillSplitterService {

    void splitBill(BillSplitter billSplitter);

    BillSplitter findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;
}
