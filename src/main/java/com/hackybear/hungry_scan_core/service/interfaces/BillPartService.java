package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.BillPart;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

public interface BillPartService {

    void save(BillPart billPart);

    BillPart findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;
}
