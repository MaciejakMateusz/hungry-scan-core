package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.PricePlan;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface PricePlanService {

    List<PricePlan> findAll();

    PricePlan findById(Long id) throws LocalizedException;

}