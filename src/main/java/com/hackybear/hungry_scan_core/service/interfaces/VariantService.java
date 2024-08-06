package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface VariantService {

    Variant findById(Integer id) throws LocalizedException;

    List<Variant> findAll();

    List<Variant> findAllByMenuItemId(Integer menuItemId);

    void save(Variant variant) throws Exception;

    void delete(Integer id) throws LocalizedException;
}
