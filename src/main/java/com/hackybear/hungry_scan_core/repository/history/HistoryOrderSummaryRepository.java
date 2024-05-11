package com.hackybear.hungry_scan_core.repository.history;

import com.hackybear.hungry_scan_core.entity.history.HistoryOrderSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HistoryOrderSummaryRepository extends JpaRepository<HistoryOrderSummary, Long> {

    Page<HistoryOrderSummary> findByInitialOrderDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

}