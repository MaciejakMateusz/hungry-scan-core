package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Booking;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.Settings;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.SettingsService;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class BookingValidator {

    private final RestaurantTableRepository restaurantTableRepository;
    private final SettingsService settingsService;

    public BookingValidator(RestaurantTableRepository restaurantTableRepository, SettingsService settingsService) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.settingsService = settingsService;
    }

    private Settings getSettings() {
        return settingsService.findByRestaurant().orElseThrow();
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
                    booking.getTime().plusHours(getSettings().getBookingDuration()),
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
        return booking.getTime().isAfter(getSettings().getOpeningTime()) &&
                booking.getTime().isBefore(getSettings().getClosingTime());
    }
}
