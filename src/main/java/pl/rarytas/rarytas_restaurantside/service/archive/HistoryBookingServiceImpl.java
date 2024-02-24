package pl.rarytas.rarytas_restaurantside.service.archive;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.archive.HistoryBooking;
import pl.rarytas.rarytas_restaurantside.repository.archive.HistoryBookingRepository;
import pl.rarytas.rarytas_restaurantside.service.archive.interfaces.HistoryBookingService;

import java.time.LocalDate;
import java.util.Set;

@Service
public class HistoryBookingServiceImpl implements HistoryBookingService {

    private final HistoryBookingRepository historyBookingRepository;

    public HistoryBookingServiceImpl(HistoryBookingRepository historyBookingRepository) {
        this.historyBookingRepository = historyBookingRepository;
    }

    @Override
    public void save(HistoryBooking historyBooking) {
        historyBookingRepository.save(historyBooking);
    }

    @Override
    public Set<HistoryBooking> findAllByWeek(int year, int week) {
        return historyBookingRepository.findAllByWeek(year, week);
    }

    @Override
    public Set<HistoryBooking> findAllByDate(LocalDate date) {
        return historyBookingRepository.findAllByDate(date);
    }
}