package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.BookingRepository;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;
import pl.rarytas.rarytas_restaurantside.utility.BookingValidator;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final BookingValidator bookingValidator;
    private final ExceptionHelper exceptionHelper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              RestaurantTableRepository restaurantTableRepository,
                              BookingValidator bookingValidator,
                              ExceptionHelper exceptionHelper) {
        this.bookingRepository = bookingRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.bookingValidator = bookingValidator;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    @Transactional
    public void save(Booking booking) throws LocalizedException {
        if (bookingValidator.isValidBooking(booking)) {
            bookingRepository.saveAndFlush(booking);
            RestaurantTable table = restaurantTableRepository.findById(booking.getTableId()).orElseThrow();
            table.getBookings().add(booking);
            restaurantTableRepository.save(table);
        } else {
            exceptionHelper.throwLocalizedMessage("error.bookingService.general.bookingCollides");
        }
    }

    @Override
    public void delete(Booking booking) {
        bookingRepository.delete(booking);
    }

    @Override
    public Set<Booking> findAllByWeek(int year, int week) {
        return bookingRepository.findAllByWeek(year, week);
    }

    @Override
    public Set<Booking> findAllByDate(LocalDate date) {
        return bookingRepository.findAllByDate(date);
    }
}