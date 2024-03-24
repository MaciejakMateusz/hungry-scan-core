package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.BookingRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BookingService;
import pl.rarytas.rarytas_restaurantside.utility.BookingValidator;

import java.time.LocalDate;

@Slf4j
@Service
public class BookingServiceImp implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;
    private final ExceptionHelper exceptionHelper;

    public BookingServiceImp(BookingRepository bookingRepository,
                             BookingValidator bookingValidator,
                             ExceptionHelper exceptionHelper) {
        this.bookingRepository = bookingRepository;
        this.bookingValidator = bookingValidator;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public Booking findById(Long id) throws LocalizedException {
        return bookingRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.bookingService.bookingNotFound", id));
    }

    @Override
    @Transactional
    public void save(Booking booking) throws LocalizedException {
        if (bookingValidator.isValidBooking(booking)) {
            bookingRepository.saveAndFlush(booking);
        } else {
            exceptionHelper.throwLocalizedMessage("error.bookingService.bookingCollides");
        }
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        Booking existingBooking = findById(id);
        bookingRepository.delete(existingBooking);
    }

    @Override
    public Page<Booking> findAllByDateBetween(Pageable pageable, LocalDate dateFrom, LocalDate dateTo) {
        return bookingRepository.findAllByDateBetween(pageable, dateFrom, dateTo);
    }

    @Override
    public Long countAll() {
        return bookingRepository.count();
    }

    @Override
    public Long countByDateBetween(LocalDate dateFrom, LocalDate dateTo) {
        return bookingRepository.countAllByDateBetween(dateFrom, dateTo);
    }
}