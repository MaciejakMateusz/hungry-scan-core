package pl.rarytas.rarytas_restaurantside.service.archive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryOrder;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.archive.HistoryOrderRepository;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryOrderService;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class HistoryOrderServiceImpl implements HistoryOrderService {

    private final HistoryOrderRepository historyOrderRepository;
    private final ExceptionHelper exceptionHelper;

    public HistoryOrderServiceImpl(HistoryOrderRepository historyOrderRepository,
                                   ExceptionHelper exceptionHelper) {
        this.historyOrderRepository = historyOrderRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<HistoryOrder> findAllForTakeAway(Pageable pageable) {
        return historyOrderRepository.findAllForTakeAway(pageable).stream().toList();
    }

    @Override
    public List<HistoryOrder> findAllDineIn(Pageable pageable) {
        return historyOrderRepository.findAllDineIn(pageable).stream().toList();
    }

    @Override
    public HistoryOrder findById(Long id) throws LocalizedException {
        return historyOrderRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.general.orderNotfound", id));
    }

    @Override
    public Long countAll() {
        return historyOrderRepository.count();
    }

    @Override
    public List<HistoryOrder> findDineInByDate(Pageable pageable, LocalDate startDate, LocalDate endDate) {
        return historyOrderRepository.findDineInByDates(pageable, startDate, endDate).stream().toList();
    }

    @Override
    public List<HistoryOrder> findTakeAwayByDate(Pageable pageable, LocalDate startDate, LocalDate endDate) {
        return historyOrderRepository.findTakeAwayByDates(pageable, startDate, endDate).stream().toList();
    }

    @Override
    public void save(HistoryOrder historyOrder) {
        historyOrderRepository.save(historyOrder);
    }
}