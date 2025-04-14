package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.PricePlan;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.PricePlanRepository;
import com.hackybear.hungry_scan_core.service.interfaces.PricePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricePlanServiceImp implements PricePlanService {

    private final PricePlanRepository pricePlanRepository;
    private final ExceptionHelper exceptionHelper;

    @Override
    public List<PricePlan> findAll() {
        return pricePlanRepository.findAll();
    }

    @Override
    public PricePlan findById(String id) throws LocalizedException {
        return pricePlanRepository.findById(id).orElseThrow(
                exceptionHelper.supplyLocalizedMessage("error.pricePlanService.planNotFound"));
    }
}
