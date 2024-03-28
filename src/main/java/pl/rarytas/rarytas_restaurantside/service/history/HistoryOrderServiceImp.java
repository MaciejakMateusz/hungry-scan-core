package pl.rarytas.rarytas_restaurantside.service.history;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Feedback;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.history.HistoryOrderRepository;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryOrderService;

import java.time.LocalDate;

@Service
@Slf4j
public class HistoryOrderServiceImp implements HistoryOrderService {

    private final HistoryOrderRepository historyOrderRepository;
    private final ExceptionHelper exceptionHelper;

    public HistoryOrderServiceImp(HistoryOrderRepository historyOrderRepository,
                                  ExceptionHelper exceptionHelper) {
        this.historyOrderRepository = historyOrderRepository;
        this.exceptionHelper = exceptionHelper;
    }

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

    @Override
    public void leaveFeedback(Feedback feedback) throws LocalizedException {
        HistoryOrder existingOrder = findById(feedback.getOrderId());
        existingOrder.setFeedback(feedback);
        save(existingOrder);
    }
}