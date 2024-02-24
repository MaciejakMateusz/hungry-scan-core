package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class BookingValidator {

    private final RestaurantService restaurantService;
    private final RestaurantTableRepository restaurantTableRepository;
    private final Environment environment;

    public BookingValidator(RestaurantService restaurantService, RestaurantTableRepository restaurantTableRepository, Environment environment) {
        this.restaurantService = restaurantService;
        this.restaurantTableRepository = restaurantTableRepository;
        this.environment = environment;
    }

    public boolean isValidBooking(Booking booking) {
        return DateTimeHelper.isNotInPast(LocalDateTime.of(booking.getDate(), booking.getTime())) &&
                !collidesWithExistingBooking(booking) &&
                isWithinOpeningHours(booking);
    }

    private boolean collidesWithExistingBooking(Booking booking) {
        boolean bookingCollision = false;
        RestaurantTable restaurantTable = restaurantTableRepository.findById(booking.getTableId()).orElseThrow();
        Set<Booking> existingBookings = restaurantTable.getBookings();
        for (Booking existingBooking : existingBookings) {
            boolean isOnExistingBookingDay = existingBooking.getDate().equals(booking.getDate());
            boolean isInBookingTimeRange = DateTimeHelper.isInTimeRange(
                    booking.getTime(),
                    existingBooking.getTime(),
                    existingBooking.getExpirationTime());
            boolean bookingIntersects = DateTimeHelper.timesIntersect(
                    booking.getTime(),
                    existingBooking.getTime(),
                    existingBooking.getExpirationTime());
            boolean bookingTimesCollide = isInBookingTimeRange || bookingIntersects;
            if (isOnExistingBookingDay && bookingTimesCollide) {
                bookingCollision = true;
                break;
            }
        }
        return bookingCollision;
    }

    private boolean isWithinOpeningHours(Booking booking) {
        String restaurantId = environment.getProperty("RESTAURANT_ID");
        assert restaurantId != null;
        Restaurant restaurant = restaurantService.findById(Integer.valueOf(restaurantId)).orElseThrow();
        return booking.getTime().isAfter(restaurant.getOpening()) && booking.getTime().isBefore(restaurant.getClosing());
    }
}
