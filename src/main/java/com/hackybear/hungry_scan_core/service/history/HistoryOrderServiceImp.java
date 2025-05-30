package com.hackybear.hungry_scan_core.service.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryOrder;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.history.HistoryOrderRepository;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HistoryOrderServiceImp implements HistoryOrderService {

    private final HistoryOrderRepository historyOrderRepository;
    private final ExceptionHelper exceptionHelper;

    @Override
    public Page<HistoryOrder> findAllForTakeAway(Pageable pageable) {
        return historyOrderRepository.findAllForTakeAway(pageable);
    }

    @Override
    public Page<HistoryOrder> findAllDineIn(Pageable pageable) {
        return historyOrderRepository.findAllDineIn(pageable);
    }

    @Override
    public HistoryOrder findById(Long id) throws LocalizedException {
        return historyOrderRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.orderNotFound", id));
    }

    @Override
    public Long countAll() {
        return historyOrderRepository.count();
    }


    @Override
    public Page<HistoryOrder> findDineInByDate(Pageable pageable, LocalDate dateFrom, LocalDate dateTo) {
        return historyOrderRepository.findDineInByDates(pageable, dateFrom, dateTo);
    }

    @Override
    public Page<HistoryOrder> findTakeAwayByDate(Pageable pageable, LocalDate dateFrom, LocalDate dateTo) {
        return historyOrderRepository.findTakeAwayByDates(pageable, dateFrom, dateTo);
    }

    @Override
    public void save(HistoryOrder historyOrder) {
        historyOrderRepository.save(historyOrder);
    }

}