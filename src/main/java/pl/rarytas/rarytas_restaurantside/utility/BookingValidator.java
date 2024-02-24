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

    public boolean isValidBooking(Booking booking) {
        return DateTimeHelper.isNotInPast(LocalDateTime.of(booking.getDate(), booking.getTime())) &&
                !hasBookingCollision(booking) &&
                isWithinOpeningHours(booking);
    }

    private boolean hasBookingCollision(Booking booking) {
        RestaurantTable restaurantTable = restaurantTableRepository.findById(booking.getTableId()).orElseThrow();
        Set<Booking> existingBookings = restaurantTable.getBookings();

        return existingBookings.stream()
                .anyMatch(existingBooking -> existingBooking.getDate().equals(booking.getDate()) &&
                        (existingBooking.getTime().equals(booking.getTime()) ||
                                isInBookingTimeRange(booking, existingBooking) ||
                                bookingIntersects(booking, existingBooking)));
    }

    private boolean isInBookingTimeRange(Booking booking, Booking existingBooking) {
        return DateTimeHelper.isInTimeRange(
                booking.getTime(),
                existingBooking.getTime(),
                existingBooking.getExpirationTime());
    }

    private boolean bookingIntersects(Booking booking, Booking existingBooking) {
        return DateTimeHelper.doTimeRangesOverlap(
                booking.getTime().plusHours(getSettings().getBookingDuration()),
                existingBooking.getTime(),
                existingBooking.getExpirationTime());
    }

    private boolean isWithinOpeningHours(Booking booking) {
        return booking.getTime().isAfter(getSettings().getOpeningTime()) &&
                booking.getTime().isBefore(getSettings().getClosingTime());
    }

    private Settings getSettings() {
        return settingsService.getSettings();
    }
}