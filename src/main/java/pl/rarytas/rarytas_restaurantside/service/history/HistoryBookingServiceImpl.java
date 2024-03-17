package pl.rarytas.rarytas_restaurantside.service.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryBooking;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.history.HistoryBookingRepository;
import pl.rarytas.rarytas_restaurantside.service.history.interfaces.HistoryBookingService;

import java.time.LocalDate;

@Service
public class HistoryBookingServiceImpl implements HistoryBookingService {

    private final HistoryBookingRepository historyBookingRepository;
    private final ExceptionHelper exceptionHelper;

    public HistoryBookingServiceImpl(HistoryBookingRepository historyBookingRepository, ExceptionHelper exceptionHelper) {
        this.historyBookingRepository = historyBookingRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public HistoryBooking findById(Long id) throws LocalizedException {
        return historyBookingRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.bookingService.bookingNotFound", id));
    }

    @Override
    public Page<HistoryBooking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo) {
        return historyBookingRepository.findAllByDateBetween(pageable, dateFrom, dateTo);
    }

    @Override
    public Long countAll() {
        return historyBookingRepository.count();
    }

    @Override
    public Long countByDateBetween(LocalDate dateFrom, LocalDate dateTo) {
        return historyBookingRepository.countAllByDateBetween(dateFrom, dateTo);
    }

}