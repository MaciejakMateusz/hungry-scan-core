package pl.rarytas.rarytas_restaurantside.service.interfaces;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.enums.DayPart;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.BookingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService{

    private final RestaurantService restaurantService;
    private final BookingRepository bookingRepository;
    private final Environment environment;
    private final MessageSource messageSource;

    public BookingServiceImpl(RestaurantService restaurantService, BookingRepository bookingRepository, Environment environment, MessageSource messageSource) {
        this.restaurantService = restaurantService;
        this.bookingRepository = bookingRepository;
        this.environment = environment;
        this.messageSource = messageSource;
    }

    @Override
    public void save(Booking booking) {
        try {
            DayPart dayPart = computeDayPart(booking.getTime());
            booking.setDayPart(dayPart);
        } catch (LocalizedException e) {
            log.warn("Wrong booking time.");
        }
        bookingRepository.save(booking);
    }

    @Override
    public void delete(Booking booking) {

    }

    @Override
    public List<Booking> findAllByWeek(int year, int week) {
        return bookingRepository.findAllByWeek(year, week);
    }

    @Override
    public List<Booking> findAllByDate(LocalDate date) {
        return bookingRepository.findAllByDate(date);
    }

    private DayPart computeDayPart(LocalTime time) throws LocalizedException {
        Integer restaurantId = Integer.valueOf(Objects.requireNonNull(environment.getProperty("RESTAURANT_ID")));
        Restaurant restaurant = restaurantService.findById(restaurantId).orElseThrow();

        LocalTime firstHalfStart = restaurant.getOpening();
        LocalTime firstHalfEnd = computeAvgTime(restaurant.getOpening(), restaurant.getClosing());
        LocalTime secondHalfStart = firstHalfEnd.plusMinutes(1);
        LocalTime secondHalfEnd = restaurant.getClosing();

        if (time.isAfter(firstHalfStart) && time.isBefore(firstHalfEnd)) {
            return DayPart.FIRST_HALF;
        } else if (time.isAfter(secondHalfStart) && time.isBefore(secondHalfEnd)) {
            return DayPart.SECOND_HALF;
        } else {
            throw new LocalizedException(String.format(messageSource.getMessage(
                    "error.bookingService.general.wrongBookingTime",
                    null, LocaleContextHolder.getLocale())));
        }
    }

    private LocalTime computeAvgTime(LocalTime opening, LocalTime closing) {
        long openingSecondOfDay = opening.toSecondOfDay();
        long closingSecondOfDay = closing.toSecondOfDay();
        long averageTotalSeconds = (openingSecondOfDay + closingSecondOfDay) / 2;
        return LocalTime.ofSecondOfDay(averageTotalSeconds);
    }
}
