package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.BillSplitter;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.BillSplitterRepository;
import com.hackybear.hungry_scan_core.service.interfaces.BillSplitterService;
import org.springframework.stereotype.Service;

@Service
public class BillSplitterServiceImp implements BillSplitterService {

    private final BillSplitterRepository billSplitterRepository;
    private final ExceptionHelper exceptionHelper;

    public BillSplitterServiceImp(BillSplitterRepository billSplitterRepository, ExceptionHelper exceptionHelper) {
        this.billSplitterRepository = billSplitterRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void splitBill(BillSplitter billSplitter) {
        billSplitterRepository.save(billSplitter);
    }

    @Override
    public BillSplitter findById(Long id) throws LocalizedException {
        return billSplitterRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.billSplitterService.splitterNotFound", id));
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        BillSplitter billSplitter = findById(id);
        billSplitterRepository.delete(billSplitter);
    }
}
