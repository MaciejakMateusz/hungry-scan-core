package pl.rarytas.rarytas_restaurantside.repository.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrderSummary;

import java.time.LocalDate;

public interface HistoryOrderSummaryRepository extends JpaRepository<HistoryOrderSummary, Long> {

    Page<HistoryOrderSummary> findByInitialOrderDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo);

}