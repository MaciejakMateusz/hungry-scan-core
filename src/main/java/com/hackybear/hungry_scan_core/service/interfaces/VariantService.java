package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface VariantService {

    void save(Variant variant);

    List<Variant> findAll();

    Variant findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}
