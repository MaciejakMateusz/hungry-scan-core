package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.BillSplitter;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

public interface BillSplitterService {

    void splitBill(BillSplitter billSplitter);

    BillSplitter findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;

}
