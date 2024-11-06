package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.BillPart;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.BillPartRepository;
import com.hackybear.hungry_scan_core.service.interfaces.BillPartService;
import org.springframework.stereotype.Service;

@Service
public class BillPartServiceImp implements BillPartService {

    private final BillPartRepository billPartRepository;
    private final ExceptionHelper exceptionHelper;

    public BillPartServiceImp(BillPartRepository billPartRepository, ExceptionHelper exceptionHelper) {
        this.billPartRepository = billPartRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(BillPart billPart) {
        billPartRepository.save(billPart);
    }

    @Override
    public BillPart findById(Long id) throws LocalizedException {
        return billPartRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.billPartService.billPartNotFound", id));
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        BillPart billPart = findById(id);
        billPartRepository.delete(billPart);
    }
}
