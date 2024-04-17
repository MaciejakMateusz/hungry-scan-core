package pl.rarytas.rarytas_restaurantside.service.history;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Feedback;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrderSummary;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.history.HistoryOrderSummaryRepository;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderSummaryService;

import java.time.LocalDate;

@Service
@Slf4j
public class HistoryOrderSummaryServiceImp implements HistoryOrderSummaryService {

    private final HistoryOrderSummaryRepository historyOrderSummaryRepository;
    private final ExceptionHelper exceptionHelper;

    public HistoryOrderSummaryServiceImp(HistoryOrderSummaryRepository historyOrderSummaryRepository,
                                         ExceptionHelper exceptionHelper) {
        this.historyOrderSummaryRepository = historyOrderSummaryRepository;
        this.exceptionHelper = exceptionHelper;
    }

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

    @Override
    public void leaveFeedback(Feedback feedback) throws LocalizedException {
        HistoryOrderSummary existingSummary = findById(feedback.getSummaryId());
        existingSummary.setFeedback(feedback);
        save(existingSummary);
    }
}