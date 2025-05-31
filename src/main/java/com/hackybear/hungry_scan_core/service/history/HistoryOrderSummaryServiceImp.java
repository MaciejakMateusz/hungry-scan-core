package com.hackybear.hungry_scan_core.service.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryOrderSummary;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.history.HistoryOrderSummaryRepository;
import com.hackybear.hungry_scan_core.service.history.interfaces.HistoryOrderSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HistoryOrderSummaryServiceImp implements HistoryOrderSummaryService {

    private final HistoryOrderSummaryRepository historyOrderSummaryRepository;
    private final ExceptionHelper exceptionHelper;

    @Override
    public HistoryOrderSummary findById(Long id) throws LocalizedException {
        return historyOrderSummaryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderSummaryService.summaryNotFound", id));
    }

    @Override
    public Long countAll() {
        return historyOrderSummaryRepository.count();
    }

    @Override
    public Page<HistoryOrderSummary> findByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo) {
        return historyOrderSummaryRepository.findByInitialOrderDateBetween(pageable, dateFrom, dateTo);
    }


    @Override
    public void save(HistoryOrderSummary historyOrderSummary) {
        historyOrderSummaryRepository.save(historyOrderSummary);
    }
}