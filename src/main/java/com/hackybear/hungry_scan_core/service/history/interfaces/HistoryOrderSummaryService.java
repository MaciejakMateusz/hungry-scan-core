package com.hackybear.hungry_scan_core.service.history.interfaces;

import com.hackybear.hungry_scan_core.entity.Feedback;
import com.hackybear.hungry_scan_core.entity.history.HistoryOrderSummary;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface HistoryOrderSummaryService {

    HistoryOrderSummary findById(Long id) throws LocalizedException;

    Long countAll();

    Page<HistoryOrderSummary> findByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

    void save(HistoryOrderSummary historyOrderSummary);

    void leaveFeedback(Feedback feedback) throws LocalizedException;
}
