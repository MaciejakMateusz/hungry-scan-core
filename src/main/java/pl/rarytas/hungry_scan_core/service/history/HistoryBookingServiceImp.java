package pl.rarytas.hungry_scan_core.service.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.history.HistoryBooking;
import pl.rarytas.hungry_scan_core.exception.ExceptionHelper;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.repository.history.HistoryBookingRepository;
import pl.rarytas.hungry_scan_core.service.history.interfaces.HistoryBookingService;

import java.time.LocalDate;

@Service
public class HistoryBookingServiceImp implements HistoryBookingService {

    private final HistoryBookingRepository historyBookingRepository;
    private final ExceptionHelper exceptionHelper;

    public HistoryBookingServiceImp(HistoryBookingRepository historyBookingRepository, ExceptionHelper exceptionHelper) {
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